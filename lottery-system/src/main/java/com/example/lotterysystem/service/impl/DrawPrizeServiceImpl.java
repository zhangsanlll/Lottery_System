package com.example.lotterysystem.service.impl;

import com.example.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.example.lotterysystem.common.exception.ServiceException;
import com.example.lotterysystem.common.utils.JacksonUtil;
import com.example.lotterysystem.common.utils.RedisUtil;
import com.example.lotterysystem.controller.DrawPrizeController;
import com.example.lotterysystem.controller.param.DrawPrizeParam;
import com.example.lotterysystem.dao.dataobject.*;
import com.example.lotterysystem.dao.mapper.*;
import com.example.lotterysystem.service.DrawPrizeService;
import com.example.lotterysystem.service.dto.PrizeDTO;
import com.example.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import com.example.lotterysystem.service.enums.ActivityStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.lotterysystem.common.config.DirectRabbitConfig.EXCHANGE_NAME;
import static com.example.lotterysystem.common.config.DirectRabbitConfig.ROUTING;

@Service
public class DrawPrizeServiceImpl implements DrawPrizeService {

    private static final Logger logger = LoggerFactory.getLogger(DrawPrizeServiceImpl.class);

    private static final Long WINNING_RECORDS_TIMEOUT = 60 * 60 *24 * 3L;
    private static final String WINNING_RECORDS_PREFIX = "WINNING_RECORDS_";
    /**
     *使⽤RabbitTemplate,这提供了接收/发送等等⽅法
     *
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private ActivityPrizeMapper activityPrizeMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PrizeMapper prizeMapper;
    @Autowired
    private WinningRecordMapper winningRecordMapper;
    @Autowired
    private RedisUtil redisUtil;


    @Override
    public void drawPrize(DrawPrizeParam param) {
        Map<String,String> map = new HashMap<>();
        map.put("messageId",String.valueOf(UUID.randomUUID()));
        map.put("messageData", JacksonUtil.writeValueAsString(param));
        //发消息：交换机、绑定的key、消息体
        rabbitTemplate.convertAndSend(EXCHANGE_NAME,ROUTING,map);
        logger.info("mq消息发送成功,map={}",JacksonUtil.writeValueAsString(map));
    }

    /**
     * 校验抽奖参数请求
     * @param param
     */
    @Override
    public void checkDrawPrizeParam(DrawPrizeParam param) {
        ActivityDO activityDO = activityMapper.selectById(param.getActivityId());
        //奖品是否存在可以从 activity_prize表中拿取, 原因是保存activity做了本地事务，保证一致性
        ActivityPrizeDO activityPrizeDO = activityPrizeMapper.selectByAPId(
                param.getActivityId(),param.getPrizeId());
        //活动或奖品是否存在
        if(null == activityDO || null == activityPrizeDO){
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_OR_PRIZE_IS_EMPTY);
        }
        //活动是否有效
        if(activityDO.getStatus()
                .equalsIgnoreCase(ActivityStatusEnum.COMPLETED.name())){
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_COMPLETED);
        }

        //奖品是否有效
        if(activityPrizeDO.getStatus()
                .equalsIgnoreCase(ActivityPrizeStatusEnum.COMPLETED.name())){
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_PRIZE_COMPLETED);
        }

        //中奖者人数是否和设置奖品数量一致
        if(activityPrizeDO.getPrizeAmount() != param.getWinnerList().size()){
            throw new ServiceException(ServiceErrorCodeConstants.WINNER_PRIZE_AMOUNT_ERROR);
        }
    }

    @Override
    public List<WinningRecordDO> saveWinnerRecords(DrawPrizeParam param) {
        //首先查询相关信息：活动、人员、奖品以及活动关联奖品表
        //通过活动id查询有关信息
        ActivityDO activityDO = activityMapper.selectById(param.getActivityId());
        //查询中奖人员相关信息，是列表
        List<UserDO> activityUserDO = userMapper.batchSelectById(
                param.getWinnerList().stream()
                        .map(DrawPrizeParam.Winner::getUserId)
                        .collect(Collectors.toList())
        );
        //查询奖品信息
        PrizeDO prizeDO = prizeMapper.selectById(param.getPrizeId());
        //
        ActivityPrizeDO activityPrizeDO = activityPrizeMapper.
                selectByAPId(param.getActivityId(), param.getPrizeId());

        //构造中奖者记录，并保存
        List<WinningRecordDO> winningRecordDOList = activityUserDO.stream()
                .map(userDO -> {
                    WinningRecordDO winningRecordDO = new WinningRecordDO();
                    winningRecordDO.setActivityId(activityDO.getId());
                    winningRecordDO.setActivityName(activityDO.getActivityName());
                    winningRecordDO.setPrizeId(prizeDO.getId());
                    winningRecordDO.setPrizeName(prizeDO.getName());
                    winningRecordDO.setPrizeTier(activityPrizeDO.getPrizeTiers());
                    winningRecordDO.setWinnerEmail(userDO.getEmail());
                    winningRecordDO.setWinnerId(userDO.getId());
                    winningRecordDO.setWinnerName(userDO.getUserName());
                    winningRecordDO.setWinnerPhoneNumber(userDO.getPhoneNumber());
                    winningRecordDO.setWinnerTime(param.getWinningTime());
                    return winningRecordDO;
                }).collect(Collectors.toList());
        winningRecordMapper.batchInsert(winningRecordDOList);

        //缓存中奖者记录
        //1、缓存奖品维度中奖记录(WinningRecord_activityId_prizeId, winningRecordDOList（奖品维度的中奖名单）)
        cacheWinningRecords(param.getActivityId()+"_"+ param.getPrizeId(),
                winningRecordDOList,WINNING_RECORDS_TIMEOUT);
        // 2、缓存活动维度中奖记录(WinningRecord_activityId, winningRecordDOList(活动维度的中奖名单))
        // 当活动已完成再去存放活动维度中奖记录
        if(activityDO.getStatus()
                .equalsIgnoreCase(ActivityStatusEnum.COMPLETED.name())){
            //查询活动维度的全量中奖记录
            List<WinningRecordDO> allList = winningRecordMapper.selectByActivityId(param.getActivityId());
            cacheWinningRecords(String.valueOf(param.getActivityId()),
                    allList,WINNING_RECORDS_TIMEOUT);
        }
        return winningRecordDOList;
    }

    /**
     * 缓存中奖记录到redis中
     * @param key
     * @param winningRecordDOList
     * @param time
     */
    private void cacheWinningRecords(String key,
                                     List<WinningRecordDO> winningRecordDOList,
                                     Long time) {
        String str = "";
        try{
            if(!StringUtils.hasText(key) || CollectionUtils.isEmpty(winningRecordDOList)){
                logger.warn("要缓存的内容为空！key:{},value:{}",key,
                        JacksonUtil.writeValueAsString(winningRecordDOList));
                return;
            }

            str = JacksonUtil.writeValueAsString(winningRecordDOList);
            redisUtil.set(WINNING_RECORDS_PREFIX + key,
                    str,time);
        }catch (Exception e){
            logger.error("缓存中奖记录异常！key:{},value:{}",WINNING_RECORDS_PREFIX + key,key);
        }

    }

    /**
     * 从缓存中获取中奖记录
     * @param key
     * @return
     */
    private List<WinningRecordDO> getWinningRecords(String key){
        try{
            if(!StringUtils.hasText(key)){
                logger.warn("要从缓存中查询中奖记录的key为空！");
                return Arrays.asList();
            }

            String str = redisUtil.get(WINNING_RECORDS_PREFIX+key);
            if(!StringUtils.hasText(str)){
                return Arrays.asList();
            }
            return JacksonUtil.readListValue(str,WinningRecordDO.class);
        } catch (Exception e){
            logger.error("从缓存中查询中奖记录异常！key:{}",WINNING_RECORDS_PREFIX+key);
            return Arrays.asList();
        }
    }
}
