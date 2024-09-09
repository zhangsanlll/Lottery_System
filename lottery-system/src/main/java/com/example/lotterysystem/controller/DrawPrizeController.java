package com.example.lotterysystem.controller;

import com.example.lotterysystem.common.pojo.CommonResult;
import com.example.lotterysystem.common.utils.JacksonUtil;
import com.example.lotterysystem.controller.param.DrawPrizeParam;
import com.example.lotterysystem.service.DrawPrizeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class DrawPrizeController {
    private static final Logger logger = LoggerFactory.getLogger(DrawPrizeController.class);

    @Autowired
    private DrawPrizeService drawPrizeService;
    /**
     * 异步抽奖，接口只做奖品数校验即可返回
     * @param param
     * @return
     */
    @RequestMapping("/draw-prize")
    public CommonResult<Boolean> drawPrize(@RequestBody @Validated DrawPrizeParam param){
        logger.info("drawPrize DrawPrizeParam:{}",
                JacksonUtil.writeValueAsString(param));
        drawPrizeService.drawPrize(param);
        return CommonResult.success(true);
    }
}
