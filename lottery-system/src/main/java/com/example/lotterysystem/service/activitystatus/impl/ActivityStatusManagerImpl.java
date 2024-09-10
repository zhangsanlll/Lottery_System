package com.example.lotterysystem.service.activitystatus.impl;

import com.example.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.example.lotterysystem.common.exception.ServiceException;
import com.example.lotterysystem.service.ActivityService;
import com.example.lotterysystem.service.activitystatus.ActivityStatusManager;
import com.example.lotterysystem.service.activitystatus.operator.AbstractActivityOperator;
import com.example.lotterysystem.service.dto.ConvertActivityStatusDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class ActivityStatusManagerImpl implements ActivityStatusManager {

    private static final Logger logger = LoggerFactory.getLogger(ActivityStatusManagerImpl.class);
    @Autowired
    private ActivityService activityService;
    @Autowired
    private final Map<String,AbstractActivityOperator> operatorMap = new HashMap<>();

    /**
     * 具体处理活动相关状态转换的逻辑
     * @param convertActivityStatusDTO
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handlerEvent(ConvertActivityStatusDTO convertActivityStatusDTO) {
        //1、活动状态扭转会有依赖，导致代码维护性差
        //2、状态扭转条件可能会扩展，若是按照常规写法可读性差，扩展性差，维护性差
        if(CollectionUtils.isEmpty(operatorMap)){
            logger.error("operatorMap 为空！");
            return ;
        }
        //不希望改变原有的哈希表
        Map<String,AbstractActivityOperator> currentMap = new HashMap<>(operatorMap);
        Boolean update = false;
        //先处理奖品和人员（顺序无所谓）
        update = processConvertStatus(convertActivityStatusDTO,currentMap,1);
        
        //后处理活动
        update = processConvertStatus(convertActivityStatusDTO,currentMap,2) || update;
        
        //更新缓存
        if(update){
            
            activityService.cacheActivity(convertActivityStatusDTO.getActivityId());
        }

    }

    @Override
    public void rollbackHandlerEvent(ConvertActivityStatusDTO convertActivityStatusDTO) {

    }

    private Boolean processConvertStatus(ConvertActivityStatusDTO convertActivityStatusDTO,
                                         Map<String, AbstractActivityOperator> currentMap,
                                         int sequence) {
        Boolean update = false;
        //遍历currentMap
        Iterator<Map.Entry<String,AbstractActivityOperator>> iterator = currentMap.entrySet().iterator();
        while(iterator.hasNext()){
            AbstractActivityOperator operator = iterator.next().getValue();
            //Operator石是否需要转换
            if(operator.sequence() != sequence
            || ! operator.needConvert(convertActivityStatusDTO)){
                continue;
            }
        //需要转换
            if(!operator.convert(convertActivityStatusDTO)){
                logger.error("{}状态转换失败！",operator.getClass().getName());
                throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_STATUS_CONVERT_FAIL);
            }

            //currentMap删除当前Operator
            iterator.remove();
            update = true;
        }

        //返回
        return update;
    }
}
