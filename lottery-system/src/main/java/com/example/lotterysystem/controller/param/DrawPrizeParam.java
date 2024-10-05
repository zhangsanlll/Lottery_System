package com.example.lotterysystem.controller.param;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class DrawPrizeParam implements Serializable {
    /**
     * 活动id
     */
    @NotNull(message = "活动id不能为空！")
    private Long activityId;
    /**
     * 奖品id
     */
    @NotNull(message = "奖品id不能为空！")
    private Long prizeId;
    /**
     * 获奖时间
     */
    @NotNull(message = "获奖时间不能为空！")
    private Date winningTime;
    /**
     * 获奖人员列表
     */
    @NotEmpty(message = "获奖人员列表不能为空！")
    @Valid
    private List<Winner> winnerList;

    @Data
    public static class Winner{
        /**
         * 获奖者id
         */
        @NotNull(message = "获奖者id不能为空！")
        private Long userId;
        /**
         * 获奖者姓名
         */
        @NotBlank(message = "获奖者姓名不能为空！")
        private String userName;
    }
}
