package com.example.lotterysystem.service;

import com.example.lotterysystem.controller.param.DrawPrizeParam;
import com.example.lotterysystem.dao.dataobject.WinningRecordDO;

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
    void checkDrawPrizeParam(DrawPrizeParam param);

    /**
     * 保存中奖信息
     * @param param
     * @return
     */
    List<WinningRecordDO> saveWinnerRecords(DrawPrizeParam param);
}
