package com.example.lotterysystem.controller.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class CreatePrizeByActivityParam implements Serializable {
    /**
     * 相关活动奖品id
     */
    @NotNull(message = "相关活动奖品id不能为空！")
    private Long prizeId;
    /**
     * 奖品数量
     */
    @NotNull(message = "奖品数量不能为空！")
    private Long prizeAmount;
    /**
     * 奖品等级
     */
    @NotBlank(message = "奖品等级不能为空!")
    private String prizeTires;
}
