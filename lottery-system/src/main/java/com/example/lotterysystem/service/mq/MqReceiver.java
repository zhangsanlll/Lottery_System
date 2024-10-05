package com.example.lotterysystem.service.mq;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.example.lotterysystem.common.exception.ServiceException;
import com.example.lotterysystem.common.utils.JacksonUtil;
import com.example.lotterysystem.common.utils.MailUtil;
import com.example.lotterysystem.common.utils.SMSUtil;
import com.example.lotterysystem.controller.param.DrawPrizeParam;
import com.example.lotterysystem.dao.dataobject.ActivityPrizeDO;
import com.example.lotterysystem.dao.dataobject.WinningRecordDO;
import com.example.lotterysystem.dao.mapper.ActivityPrizeMapper;
import com.example.lotterysystem.dao.mapper.WinningRecordMapper;
import com.example.lotterysystem.service.DrawPrizeService;
import com.example.lotterysystem.service.activitystatus.ActivityStatusManager;
import com.example.lotterysystem.service.dto.ConvertActivityStatusDTO;
import com.example.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import com.example.lotterysystem.service.enums.ActivityPrizeTiersStatusEnum;
import com.example.lotterysystem.service.enums.ActivityStatusEnum;
import com.example.lotterysystem.service.enums.ActivityUserStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;


import java.rmi.server.ServerCloneException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.lotterysystem.common.config.DirectRabbitConfig.QUEUE_NAME;

@Component
@RabbitListener(queues = QUEUE_NAME)//告诉mq要监听的是哪个队列
public class MqReceiver {
    private static final Logger logger = LoggerFactory.getLogger(MqReceiver.class);

    @Autowired
    private DrawPrizeService drawPrizeService;
    @Autowired
    private ActivityPrizeMapper activityPrizeMapper;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private MailUtil mailUtil;
    @Autowired
    private SMSUtil smsUtil;
    @Autowired
    private ActivityStatusManager activityStatusManager;
    @Autowired
    private WinningRecordMapper winningRecordMapper;

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
            /**
             * 有可能会出现这样一个问题：用户手快同时点击了多次，这样前端就有可能会发起两个或两个以上的抽奖请求
             * 对于param来说是一样的请求
             * 当parameter只剩最后一个奖项时：
             * 处理param1: 活动完成，奖品完成
             * 处理param2：就会抛出异常，进入回滚操作，回滚奖品、活动状态，此时就会出现bug
             */
            if(!drawPrizeService.checkDrawPrizeParam(param)){
                return ;
            }

            //状态扭转处理(加入设计模式)
            statusConvert(param);

            //保存中奖者名单
            List<WinningRecordDO> winningRecordDOList = drawPrizeService.saveWinnerRecords(param);

            //通知中奖者（邮箱、短信）
            syncExecute(winningRecordDOList);
            //如果异常，需要保证事务一致性（回滚），抛出异常

        }catch (ServiceException e){
            logger.error("处理MQ消息异常！{} {}",e.getCode(),e.getMessage(),e);
            //保证事务一致性，需要进行回滚
            rollback(param);
        }catch (Exception e){
            logger.error("处理 MQ 消息异常！", e);
            //保证事务一致性，需要进行回滚
            rollback(param);
            //抛出异常
            throw e;
        }

    }

    /**
     * 处理抽奖异常的回滚行为：恢复处理请求之前的库表状态
     * @param param
     */
    private void rollback(DrawPrizeParam param) {
        //1、有哪些表需要回滚？ 活动、人员、奖品

        //判断状态是否需要回滚
        if(!statusNeedRollback(param)){
            //如果不需要，直接返回
            return;
        }

        //需要回滚
        rollbackStatus(param);

        //2、回滚中奖名单
        //判断中奖名单是否需要回滚
        if(!winnerNeedRollback(param)){
            //如果不需要，直接返回
            return;
        }

        //需要回滚
        rollbackWinner(param);
    }

    /**
     * 回滚中奖记录，删除奖品下的中奖者
     * @param param
     */
    private void rollbackWinner(DrawPrizeParam param) {
        drawPrizeService.deleteRecords(param.getActivityId(),param.getPrizeId());
    }

    /**
     *  只需判断活动中的奖品是否存在中奖者
     * @param param
     * @return
     */
    private boolean winnerNeedRollback(DrawPrizeParam param) {
        int count = winningRecordMapper.countByAPId(param.getActivityId(),param.getPrizeId());
        return count > 0;
    }

    /**
     * 回滚相关状态
     * @param param
     */
    private void rollbackStatus(DrawPrizeParam param) {
        //涉及状态的恢复，使用ActivityStatusManager
        ConvertActivityStatusDTO convertActivityStatusDTO = new ConvertActivityStatusDTO();
        convertActivityStatusDTO.setActivityId(param.getActivityId());
        convertActivityStatusDTO.setTargetActivityStatus(ActivityStatusEnum.RUNNING);
        convertActivityStatusDTO.setPrizeId(param.getPrizeId());
        convertActivityStatusDTO.setTargetPrizeStatus(ActivityPrizeStatusEnum.COMPLETED);
        convertActivityStatusDTO.setUserId(
                param.getWinnerList().stream().
                        map(DrawPrizeParam.Winner::getUserId)
                        .collect(Collectors.toList())
        );
        convertActivityStatusDTO.setTargetUserStatus(ActivityUserStatusEnum.INIT);
        activityStatusManager.rollbackHandlerEvent(convertActivityStatusDTO);

    }

    /**
     * 判断状态是否需要回滚，只需将活动和奖品关联的表查出来，然后再对比相对的状态即可
     * @param param
     * @return
     */
    private boolean statusNeedRollback(DrawPrizeParam param) {
        // 判断活动+奖品+人员表相关状态是否已经扭转（正常思路）
        // 扭转状态时，保证了事务一致性，要么都扭转了，要么都没扭转（不包含活动）：
        // 因此，只用判断人员/奖品是否扭转过，就能判断出状态是否全部扭转
        // 不能判断活动是否已经扭转
        // 结论：只要判断奖品状态是否扭转，就能判断出全部状态是否扭转
        ActivityPrizeDO activityPrizeDO = activityPrizeMapper.selectByAPId(param.getActivityId(),
                param.getPrizeId());
        //已经扭转了，需要回滚
        return activityPrizeDO.getStatus()
                .equalsIgnoreCase(ActivityPrizeStatusEnum.COMPLETED.name());
    }

    /**
     * 并发处理抽奖后续流程
     * @param winningRecordDOList
     */
    private void syncExecute(List<WinningRecordDO> winningRecordDOList) {
        // 通过线程池 threadPoolTaskExecutor
        // 扩展：加入策略模式或者其他设计模式来完成后续的异步操作
        // 短信通知
        threadPoolTaskExecutor.execute(()->sendMessage(winningRecordDOList));
        //邮件通知
        threadPoolTaskExecutor.execute(()->sendMail(winningRecordDOList));
    }

    /**
     * 发邮件
     * @param winningRecordDOList
     */
    private void sendMail(List<WinningRecordDO> winningRecordDOList) {
        //先判断
        if(CollectionUtils.isEmpty(winningRecordDOList)){
            logger.info("中奖列表为空，不用发邮件！");
            return;
        }
        for(WinningRecordDO winningRecordDO : winningRecordDOList){
            //邮件格式，如果有多个邮件就一个一个发
            String context = "Hi,"+winningRecordDO.getWinnerName()+".恭喜你在"
                    +winningRecordDO.getActivityName()+"活动中获得"+
                    ActivityPrizeTiersStatusEnum.forName(winningRecordDO.getPrizeTier()).getMessage()+
                    ":"+winningRecordDO.getPrizeName()+".获奖时间为"+ DateUtil.formatTime(winningRecordDO.getWinnerTime()) +
                    ",请尽快领取您的奖励！";
            mailUtil.sendSampleMail(winningRecordDO.getWinnerEmail(),"中奖通知",context);
        }
    }

    /**
     * 发短信
     * @param winningRecordDOList
     */
    private void sendMessage(List<WinningRecordDO> winningRecordDOList) {
        //先判断
        if(CollectionUtils.isEmpty(winningRecordDOList)){
            logger.info("中奖列表为空，不用发短信！");
            return;
        }
        for(WinningRecordDO winningRecordDO :winningRecordDOList){
            Map<String,String> map = new HashMap<>();
            map.put("name",winningRecordDO.getWinnerName());
            map.put("activityName",winningRecordDO.getActivityName());
            map.put("prizeTiers",ActivityPrizeTiersStatusEnum.forName(winningRecordDO.getPrizeTier()).getMessage());
            map.put("prizeName",winningRecordDO.getPrizeName());
            map.put("winningTime",DateUtil.formatTime(winningRecordDO.getWinnerTime()));
            smsUtil.sendMessage("SMS_472470255",winningRecordDO.getWinnerPhoneNumber().getValue(),
                    JacksonUtil.writeValueAsString(map));
        }
    }

    private boolean statusConvert(DrawPrizeParam param) {
        // 判断活动+奖品+人员表相关状态是否已经扭转（正常思路）
        // 扭转状态时，保证了事务一致性，要么都扭转了，要么都没扭转（不包含活动）：
        // 因此，只用判断人员/奖品是否扭转过，就能判断出状态是否全部扭转
        // 不能判断活动是否已经扭转
        // 结论：判断奖品状态是否扭转，就能判断出全部状态是否扭转
        ActivityPrizeDO aPDO = activityPrizeMapper.selectByAPId(param.getActivityId(), param.getPrizeId());
        //已经扭转了，需要回滚
        return aPDO.getStatus()
                .equalsIgnoreCase(ActivityPrizeStatusEnum.COMPLETED.name());
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
