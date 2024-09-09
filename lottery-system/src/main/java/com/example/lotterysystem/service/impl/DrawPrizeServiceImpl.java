package com.example.lotterysystem.service.impl;

import com.example.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.example.lotterysystem.common.exception.ServiceException;
import com.example.lotterysystem.common.utils.JacksonUtil;
import com.example.lotterysystem.controller.DrawPrizeController;
import com.example.lotterysystem.controller.param.DrawPrizeParam;
import com.example.lotterysystem.dao.dataobject.ActivityDO;
import com.example.lotterysystem.dao.dataobject.ActivityPrizeDO;
import com.example.lotterysystem.dao.mapper.ActivityMapper;
import com.example.lotterysystem.dao.mapper.ActivityPrizeMapper;
import com.example.lotterysystem.service.DrawPrizeService;
import com.example.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import com.example.lotterysystem.service.enums.ActivityStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.lotterysystem.common.config.DirectRabbitConfig.EXCHANGE_NAME;
import static com.example.lotterysystem.common.config.DirectRabbitConfig.ROUTING;

@Service
public class DrawPrizeServiceImpl implements DrawPrizeService {

    private static final Logger logger = LoggerFactory.getLogger(DrawPrizeServiceImpl.class);

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
}
