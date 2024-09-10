package com.example.lotterysystem.service.activitystatus.operator;

import com.example.lotterysystem.dao.dataobject.ActivityPrizeDO;
import com.example.lotterysystem.dao.mapper.ActivityPrizeMapper;
import com.example.lotterysystem.service.dto.ConvertActivityStatusDTO;
import com.example.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import com.example.lotterysystem.service.enums.ActivityStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrizeOperator extends AbstractActivityOperator{
    @Autowired
    private ActivityPrizeMapper activityPrizeMapper;
    @Override
    public Integer sequence() {
        return 1;
    }

    @Override
    public Boolean needConvert(ConvertActivityStatusDTO convertActivityStatusDTO) {
        Long prizeId = convertActivityStatusDTO.getPrizeId();
        Long activityId = convertActivityStatusDTO.getActivityId();
        ActivityPrizeStatusEnum targetPrizeStatus = convertActivityStatusDTO.getTargetPrizeStatus();
        if(null == activityId
                || null == prizeId
                || null == targetPrizeStatus ){
            return false;
        }
        ActivityPrizeDO activityPrizeDO = activityPrizeMapper.selectByAPId(activityId,prizeId);
        if(null == activityPrizeDO){
            return false;
        }

        //判断当前奖品状态和目标状态是否一致,如果一致，则说明不需要翻转
        if(targetPrizeStatus.name().equalsIgnoreCase(activityPrizeDO.getStatus())){
            return false;
        }
        return true;
    }

    @Override
    public Boolean convert(ConvertActivityStatusDTO convertActivityStatusDTO) {
        Long prizeId = convertActivityStatusDTO.getPrizeId();
        Long activityId = convertActivityStatusDTO.getActivityId();
        ActivityPrizeStatusEnum targetPrizeStatus = convertActivityStatusDTO.getTargetPrizeStatus();
       try{
           activityPrizeMapper.updateStatus(activityId,prizeId,targetPrizeStatus.name());
            return true;
       }catch (Exception e){
           return false;
       }

    }
}
