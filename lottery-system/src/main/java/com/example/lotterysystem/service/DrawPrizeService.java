package com.example.lotterysystem.service;

import com.example.lotterysystem.controller.param.DrawPrizeParam;
import com.example.lotterysystem.controller.param.ShowWinningRecordsParam;
import com.example.lotterysystem.dao.dataobject.WinningRecordDO;
import com.example.lotterysystem.service.dto.WinningRecordDTO;

import java.util.List;

public interface DrawPrizeService {

    /**
     *异步抽奖接口
     * @param param
     */
    void drawPrize(DrawPrizeParam param);

    /**
     * 校验抽奖请求
     * @param param
     */
    Boolean checkDrawPrizeParam(DrawPrizeParam param);

    /**
     * 保存中奖信息
     * @param param
     * @return
     */
    List<WinningRecordDO> saveWinnerRecords(DrawPrizeParam param);

    void deleteRecords(Long activityId, Long prizeId);

    /**
     * 获取中奖列表
     * @param param
     * @return
     */
    List<WinningRecordDTO> getRecords(ShowWinningRecordsParam param);
}
