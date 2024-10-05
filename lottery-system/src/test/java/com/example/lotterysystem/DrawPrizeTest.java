package com.example.lotterysystem;

import com.example.lotterysystem.controller.param.DrawPrizeParam;
import com.example.lotterysystem.controller.param.ShowWinningRecordsParam;
import com.example.lotterysystem.service.DrawPrizeService;
import com.example.lotterysystem.service.activitystatus.ActivityStatusManager;
import com.example.lotterysystem.service.dto.ConvertActivityStatusDTO;
import com.example.lotterysystem.service.dto.WinningRecordDTO;
import com.example.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import com.example.lotterysystem.service.enums.ActivityStatusEnum;
import com.example.lotterysystem.service.enums.ActivityUserStatusEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class DrawPrizeTest {

    @Autowired
    private DrawPrizeService drawPrizeService;

    @Autowired
    private ActivityStatusManager activityStatusManager;
    @Test
    void drawPrize(){
       /* DrawPrizeParam param = new DrawPrizeParam();
        param.setActivityId(8L);
        param.setPrizeId(1L);
        //param.setPrizeTiers("FIRST_PRIZE");
        param.setWinningTime(new Date());
        List<DrawPrizeParam.Winner> winnerList = new ArrayList<>();
        DrawPrizeParam.Winner winner = new DrawPrizeParam.Winner();
        winner.setUserId(1L);
        winner.setUserName("lll");
        winnerList.add(winner);
        param.setWinnerList(winnerList);
        drawPrizeService.drawPrize(param);*/


        /**
         * 需要测试以下几个方面：
         * 1.正向流程
         * 2.处理过程中发生异常的话进行回滚
         * 3.处理过程中发生异常：消息堆积->处理异常-> 消息重发
         */
        DrawPrizeParam param = new DrawPrizeParam();
        param.setActivityId(14L);
        param.setPrizeId(2L);
        param.setWinningTime(new Date());
        List<DrawPrizeParam.Winner> winnerList = new ArrayList<>();
        DrawPrizeParam.Winner winner = new DrawPrizeParam.Winner();
        winner.setUserId(12L);
        winner.setUserName("耶耶");
        winnerList.add(winner);
        param.setWinnerList(winnerList);
        drawPrizeService.drawPrize(param);
    }

    @Test
    void statusTest(){
        ConvertActivityStatusDTO convertActivityStatusDTO = new ConvertActivityStatusDTO();
        convertActivityStatusDTO.setActivityId(8L);
        convertActivityStatusDTO.setTargetActivityStatus(ActivityStatusEnum.COMPLETED);
        convertActivityStatusDTO.setPrizeId(1L);
        convertActivityStatusDTO.setTargetPrizeStatus(ActivityPrizeStatusEnum.COMPLETED);
        List<Long> userIds = Arrays.asList(9L);
        convertActivityStatusDTO.setUserId(userIds);
        convertActivityStatusDTO.setTargetUserStatus(ActivityUserStatusEnum.COMPLETED);
        activityStatusManager.handlerEvent(convertActivityStatusDTO);
    }

    @Test
    void saveWinningRecords(){
        DrawPrizeParam param = new DrawPrizeParam();
        param.setActivityId(14L);
        param.setPrizeId(1L);
        param.setWinningTime(new Date());
        List<DrawPrizeParam.Winner> winnerList = new ArrayList<>();
        DrawPrizeParam.Winner winner = new DrawPrizeParam.Winner();
        winner.setUserId(11L);
        winner.setUserName("王曼昱");
        winnerList.add(winner);
        param.setWinnerList(winnerList);
        drawPrizeService.saveWinnerRecords(param);
    }


    @Test
    void showWinningRecords() {
        ShowWinningRecordsParam param = new ShowWinningRecordsParam();
        param.setActivityId(14L);
        param.setPrizeId(1L);
        List<WinningRecordDTO> list = drawPrizeService.getRecords(param);
        for (WinningRecordDTO dto : list) {
            // 中奖者_奖品_等级
            System.out.println(dto.getWinnerName()
                    + "_" + dto.getPrizeName()
                    + "_" +dto.getPrizeTier().getMessage());
        }
    }

}
