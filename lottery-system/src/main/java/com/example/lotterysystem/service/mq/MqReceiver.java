package com.example.lotterysystem.service.mq;

import com.example.lotterysystem.common.exception.ServiceException;
import com.example.lotterysystem.common.utils.JacksonUtil;
import com.example.lotterysystem.controller.param.DrawPrizeParam;
import com.example.lotterysystem.service.DrawPrizeService;
import com.example.lotterysystem.service.dto.ConvertActivityStatusDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.rmi.server.ServerCloneException;
import java.util.Map;

import static com.example.lotterysystem.common.config.DirectRabbitConfig.QUEUE_NAME;

@Component
@RabbitListener(queues = QUEUE_NAME)//告诉mq要监听的是哪个队列
public class MqReceiver {
    private static final Logger logger = LoggerFactory.getLogger(MqReceiver.class);

    @Autowired
    private DrawPrizeService drawPrizeService;

    @RabbitHandler
    public void process(Map<String,String> message){
        //成功接收到队列中的消息
        logger.info("MQ成功接收到消息，message:{}",
                JacksonUtil.writeValueAsString(message));
        String paramString = message.get("messageData");
        DrawPrizeParam param = JacksonUtil.readListValue(paramString,DrawPrizeParam.class);
        // 处理抽奖的流程
        try{
            //校验抽奖请求是否有效
            drawPrizeService.checkDrawPrizeParam(param);
            //状态扭转处理(十分重要，会加入设计模式)
            statusConvert(param);
            //通知中奖者（邮箱、短信）

            //如果异常，需要保证事务一致性（回滚），抛出异常

        }catch (ServiceException e){
            logger.error("处理MQ消息异常！{} {}",e.getCode(),e.getMessage(),e);
        }catch (Exception e){
            logger.error("处理 MQ 消息异常！", e);
        }

    }

    private void statusConvert(DrawPrizeParam param) {
        ConvertActivityStatusDTO convertActivityStatusDTO = new ConvertActivityStatusDTO();

    }

//    private void statusConvert(DrawPrizeParam param) {
//
//        // 问题：
//        // 1、活动状态扭转有依赖性，导致代码维护性差
//        // 2、状态扭转条件可能会扩展，当前写法，扩展性差，维护性差
//        // 3、代码的灵活性、扩展性、维护性极差
//        // 解决方案：设计模式（责任链设计模式、策略模式）
//
//
//        // 活动: RUNNING-->COMPLETED ??  全部奖品抽完之后才改变状态
//        // 奖品：INIT-->COMPLETED
//        // 人员列表: INIT-->COMPLETED
//
//        // 1 扭转奖品状态
//        // 查询活动关联的奖品信息
//        // 条件判断是否符合扭转奖品状态：判断当前状态是否 不是COMPLETED，如果不是，要扭转
//        // 才去扭转
//
//        // 2 扭转人员状态
//        // 查询活动关联的人员信息
//        // 条件判断是否符合扭转人员状态：判断当前状态是否 不是COMPLETED，如果不是，要扭转
//        // 才去扭转
//
//        // 4、扭转xxx状态
//
//        // 3 扭转活动状态（必须再扭转奖品状态之后完成）
//        // 查询活动信息
//        // 条件判断是否符合扭转活动状态：才改变状态
//        //      判断当前状态是否 不是COMPLETED，如果不是，
//        //      且全部奖品抽完之后，
//        //      （且xxx状态扭转）
//        // 才去扭转
//
//
//        // 4 更新活动完整信息缓存
//
//    }



}
