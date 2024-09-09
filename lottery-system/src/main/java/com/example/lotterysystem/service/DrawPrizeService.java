package com.example.lotterysystem.service;

import com.example.lotterysystem.controller.param.DrawPrizeParam;

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
}
