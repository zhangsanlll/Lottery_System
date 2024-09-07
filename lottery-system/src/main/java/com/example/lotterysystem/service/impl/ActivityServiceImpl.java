package com.example.lotterysystem.service.impl;

import com.example.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.example.lotterysystem.common.exception.ServiceException;
import com.example.lotterysystem.common.utils.JacksonUtil;
import com.example.lotterysystem.common.utils.RedisUtil;
import com.example.lotterysystem.controller.param.CreateActivityParam;
import com.example.lotterysystem.controller.param.CreatePrizeByActivityParam;
import com.example.lotterysystem.controller.param.CreateUserByActivityParam;
import com.example.lotterysystem.dao.dataobject.ActivityDO;
import com.example.lotterysystem.dao.dataobject.ActivityPrizeDO;
import com.example.lotterysystem.dao.dataobject.ActivityUserDO;
import com.example.lotterysystem.dao.dataobject.PrizeDO;
import com.example.lotterysystem.dao.mapper.*;
import com.example.lotterysystem.service.ActivityService;
import com.example.lotterysystem.service.dto.ActivityDetailDTO;
import com.example.lotterysystem.service.dto.CreateActivityDTO;
import com.example.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import com.example.lotterysystem.service.enums.ActivityPrizeTiersStatusEnum;
import com.example.lotterysystem.service.enums.ActivityStatusEnum;
import com.example.lotterysystem.service.enums.ActivityUserStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ActivityServiceImpl implements ActivityService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);

    /**
     * 活动缓存前缀，为了区分业务
     */
    private final String ACTIVITY_PREFIX = "ACTIVITY_";
    /**
     * 活动缓存过期时间
     */
    private final Long ACTIVITY_TIMEOUT = 60 * 60 * 24 * 3L;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PrizeMapper prizeMapper;
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private ActivityPrizeMapper activityPrizeMapper;
    @Autowired
    private ActivityUserMapper activityUserMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Transactional(rollbackFor = Exception.class)
    //由于涉及到多表，所以把它归并到一个事务，若是有语句未成功运行，直接回滚
    @Override
    public CreateActivityDTO createActivity(CreateActivityParam param) {
        //校验活动信息是否正确
        checkActivityInfo(param);

        // 保存活动信息
        ActivityDO activityDO = new ActivityDO();
        activityDO.setActivityName(param.getActivityName());
        activityDO.setDescription(param.getDescription());
        activityDO.setStatus(ActivityStatusEnum.RUNNING.name());
        activityMapper.insert(activityDO);

        //保存活动关联的奖品信息
        List<CreatePrizeByActivityParam> prizeParams = param.getActivityPrizeList();
        List<ActivityPrizeDO> activityPrizeDOList = prizeParams.stream()
                .map(prizeParam ->{
                    ActivityPrizeDO activityPrizeDO = new ActivityPrizeDO();
                    activityPrizeDO.setActivityId(activityDO.getId());
                    activityPrizeDO.setPrizeId(prizeParam.getPrizeId());
                    activityPrizeDO.setPrizeAmount(prizeParam.getPrizeAmount());
                    activityPrizeDO.setPrizeTiers(prizeParam.getPrizeTires());
                    activityPrizeDO.setStatus(ActivityPrizeStatusEnum.INIT.name());
                    return activityPrizeDO;
                }).collect(Collectors.toList());
        activityPrizeMapper.batchInsert(activityPrizeDOList);

        //保存活动关联的人员信息
        List<CreateUserByActivityParam> userParams = param.getActivityUserList();
        List<ActivityUserDO> activityUserDOList = userParams.stream()
                .map(userParam ->{
                    ActivityUserDO activityUserDO = new ActivityUserDO();
                    activityUserDO.setActivityId(activityDO.getId());
                    activityUserDO.setUserId(userParam.getUserId());
                    activityUserDO.setUserName(userParam.getUserName());
                    activityUserDO.setStatus(ActivityUserStatusEnum.INIT.name());
                    return activityUserDO;
                }).collect(Collectors.toList());
        activityUserMapper.batchInsert(activityUserDOList);


        //整合完整的活动信息，并存放在redis中
        //通过activityId 直接定位ActivityDetailDTO:活动+奖品+人员

        //先获取奖品基本属性列表
        //获取需要查询的奖品id
        List<Long> prizeIds = param.getActivityPrizeList().stream()
                .map(CreatePrizeByActivityParam::getPrizeId)
                .distinct().collect(Collectors.toList());
        List<PrizeDO> prizeDOList = prizeMapper.batchSelectByIds(prizeIds);

        ActivityDetailDTO detailDTO = convertActivityDetailDTO(activityDO,activityUserDOList,
                prizeDOList,activityPrizeDOList);
        cacheActivity(detailDTO);
        //构造返回
        CreateActivityDTO createActivityDTO = new CreateActivityDTO();
        createActivityDTO.setActivityId(activityDO.getId());
        return createActivityDTO;
    }

    /**
     * 缓存完整的活动信息 ActivityDetailDTO
     * @param detailDTO
     */
    private void cacheActivity(ActivityDetailDTO detailDTO) {
        // key: ACTIVITY_12  前缀加上活动id,方便存储在缓存中不会混淆
        // value: ActivityDetailDTO(json)
        if(null == detailDTO || null == detailDTO.getActivityId()){
            logger.warn("要缓存的信息不存在！");
            return;
        }

        try {
            redisUtil.set(ACTIVITY_PREFIX+detailDTO.getActivityId(),
                    JacksonUtil.writeValueAsString(detailDTO),ACTIVITY_TIMEOUT);

        }catch (Exception e){
            logger.error("活动缓存异常，ActivityDetailDTO = {}",
                    JacksonUtil.writeValueAsString(detailDTO),e);
        }
    }

    /**
     * 根据活动id从缓存中获取活动详细信息
     * @param activityId
     * @return
     */
    private ActivityDetailDTO getActivityFromCache(Long activityId){
        if(null == activityId){
            logger.warn("获取缓存活动数据的activityId为空！");
            return null;
        }
        try{
            String str = redisUtil.get(ACTIVITY_PREFIX+activityId);
            if(!StringUtils.hasText(str)){
                logger.info("获取的缓存活动数据为空！ key = {}",ACTIVITY_PREFIX+activityId);
                return null;
            }
            return JacksonUtil.readListValue(str,ActivityDetailDTO.class);
        }catch (Exception e){
            logger.error("从缓存中获取活动信息异常,key={}",ACTIVITY_PREFIX+activityId,e);
            return null;
        }
    }


    /**
     * 根据基本DO整合完整的活动信息ActivityDetailDTO
     * @param activityDO
     * @param activityUserDOList
     * @param prizeDOList
     * @param activityPrizeDOList
     * @return
     */
    private ActivityDetailDTO convertActivityDetailDTO(ActivityDO activityDO,
                                                       List<ActivityUserDO> activityUserDOList,
                                                       List<PrizeDO> prizeDOList,
                                                       List<ActivityPrizeDO> activityPrizeDOList) {
        ActivityDetailDTO detailDTO = new ActivityDetailDTO();
        detailDTO.setActivityId(activityDO.getId());
        detailDTO.setActivityName(activityDO.getActivityName());
        detailDTO.setDescription(activityDO.getDescription());
        detailDTO.setStatus(ActivityStatusEnum.forName(activityDO.getStatus()));

        // apDO: {prizeId，amount, status}, {prizeId，amount, status}
        // pDO: {prizeid, name....},{prizeid, name....},{prizeid, name....}
        List<ActivityDetailDTO.PrizeDTO> prizeDTOList = activityPrizeDOList.stream()
                .map(apDO->{
                    ActivityDetailDTO.PrizeDTO prizeDTO = new ActivityDetailDTO.PrizeDTO();
                    prizeDTO.setPrizeId(apDO.getPrizeId());
                    Optional<PrizeDO> optionalPrizeDO = prizeDOList.stream().
                            filter(prizeDO -> prizeDO.getId().equals(apDO.getPrizeId()))
                            .findFirst();
                    //如果PrizeDO为空，不执行当前方法，不为空才执行
                    optionalPrizeDO.ifPresent(prizeDO -> {
                        prizeDTO.setDescription(prizeDO.getDescription());
                        prizeDTO.setPrice(prizeDO.getPrice());
                        prizeDTO.setImageUrl(prizeDO.getImageUrl());
                        prizeDTO.setName(prizeDTO.getName());
                    });
                    prizeDTO.setPrizeAmount(apDO.getPrizeAmount());
                    prizeDTO.setStatus(ActivityPrizeStatusEnum.forName(apDO.getStatus()));
                    prizeDTO.setTiers(ActivityPrizeTiersStatusEnum.forName(apDO.getPrizeTiers()));
                    return prizeDTO;
                }).collect(Collectors.toList());
        detailDTO.setPrizeDTOList(prizeDTOList);

        List<ActivityDetailDTO.UserDTO> userDTOList = activityUserDOList.stream()
                .map(auDO->{
                    ActivityDetailDTO.UserDTO userDTO = new ActivityDetailDTO.UserDTO();
                    userDTO.setUserId(auDO.getUserId());
                    userDTO.setUserName(auDO.getUserName());
                    userDTO.setStatus(ActivityUserStatusEnum.forName(auDO.getStatus()));
                    return userDTO;
                }).collect(Collectors.toList());
        detailDTO.setUserDTOList(userDTOList);
        return  detailDTO;
    }

    /**
     * 校验活动有效性
     * @param param
     */
    private void checkActivityInfo(CreateActivityParam param) {
        if(null == param){
            throw new ServiceException(ServiceErrorCodeConstants.CREATE_ACTIVITY_INFO_IS_EMPTY);
        }
        //人员id在人员表中是否存在
        //比如：1  2  3 ->   1   2()检查后
        //userIds是从前端接收回来的需要校验的人员
        List<Long> userIds = param.getActivityUserList().stream().map(CreateUserByActivityParam::getUserId)
                .distinct().collect(Collectors.toList());
        //existUserIds是在数据库中查出来的
        List<Long> existUserIds = userMapper.selectExistById(userIds);
        if(CollectionUtils.isEmpty(existUserIds)){
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_USER_ERROR);
        }
        userIds.forEach(id->{
            if(!existUserIds.contains(id)){
                throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_USER_ERROR);
            }
        });

        //奖品id在奖品表中是否存在
        List<Long> prizeIds = param.getActivityPrizeList().stream().map(CreatePrizeByActivityParam::getPrizeId)
                .distinct().collect(Collectors.toList());
        List<Long> existPrizeIds = prizeMapper.selectExistById(prizeIds);
        if(CollectionUtils.isEmpty(existPrizeIds)){
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_PRIZE_ERROR);
        }
        prizeIds.forEach(id->{
            if(!existPrizeIds.contains(id)){
                throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_PRIZE_ERROR);
            }
        });

        //人员数量需要大于等于奖品数量
        //两个奖品 2   1
        int userAmount = param.getActivityUserList().size();
        long prizeAmount = param.getActivityPrizeList().stream()
                .mapToLong(CreatePrizeByActivityParam::getPrizeAmount).sum();

        if(userAmount < prizeAmount){
            throw new ServiceException(ServiceErrorCodeConstants.USER_PRIZE_AMOUNT_ERROR);
        }

        //校验活动奖品等级有效性

        param.getActivityPrizeList().forEach(prize->{
            if(null == ActivityPrizeTiersStatusEnum.forName(prize.getPrizeTires())){
                throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_PRIZE_TIERS_ERROR);
            }
        });

    }
}
