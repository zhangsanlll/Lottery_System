package com.example.lotterysystem.controller.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data

public class ShowWinningRecordsParam implements Serializable {

    /**
     * 活动id
     */
    @NotNull(message = "活动id不能为空")
    private Long activityId;

    /**
     *
     *奖品id
     */
    @NotNull(message = "奖品id不能为空")
    private Long prizeId;
}
