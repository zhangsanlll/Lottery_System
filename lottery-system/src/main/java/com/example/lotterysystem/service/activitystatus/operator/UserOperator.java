package com.example.lotterysystem.service.activitystatus.operator;

import com.example.lotterysystem.dao.dataobject.ActivityPrizeDO;
import com.example.lotterysystem.dao.dataobject.ActivityUserDO;
import com.example.lotterysystem.dao.mapper.ActivityUserMapper;
import com.example.lotterysystem.service.dto.ConvertActivityStatusDTO;
import com.example.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import com.example.lotterysystem.service.enums.ActivityUserStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class UserOperator extends AbstractActivityOperator{
    @Autowired
    private ActivityUserMapper activityUserMapper;
    @Override
    public Integer sequence() {
        return 1;
    }

    @Override
    public Boolean needConvert(ConvertActivityStatusDTO convertActivityStatusDTO) {
        Long activityId = convertActivityStatusDTO.getActivityId();
        List<Long> userIds = convertActivityStatusDTO.getUserId();
        ActivityPrizeStatusEnum targetPrizeStatus = convertActivityStatusDTO.getTargetPrizeStatus();
        if(null == activityId
                || CollectionUtils.isEmpty(userIds)
                || null == targetPrizeStatus ){
            return false;
        }
        //查询的是抽奖人员列表
        List<ActivityUserDO> activityUserDOList = activityUserMapper.batchSelectByAUIds(activityId,userIds);
        if(null == activityUserDOList){
            return false;
        }

        //判断当前奖品状态和目标状态是否一致,如果一致，则说明不需要翻转
        //因为是列表，所以需要遍历列表进行判断
        for(ActivityUserDO auDO : activityUserDOList){
            if(auDO.getStatus()
                    .equalsIgnoreCase(targetPrizeStatus.name())){
                return false;
            }
        }
        return true;
    }

    @Override
    public Boolean convert(ConvertActivityStatusDTO convertActivityStatusDTO) {
        Long activityId = convertActivityStatusDTO.getActivityId();
        List<Long> userIds = convertActivityStatusDTO.getUserId();
        ActivityUserStatusEnum targetUserStatus = convertActivityStatusDTO.getTargetUserStatus();
        try{
            activityUserMapper.updateStatus(activityId,userIds,targetUserStatus.name());
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
