package com.example.lotterysystem.controller.param;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class CreateActivityParam implements Serializable {
    /**
     * 活动名称
     */
    @NotBlank(message = "活动名称不能为空")
    private String activityName;

    /**
     * 活动描述
     */
    @NotBlank(message = "活动描述不能为空")
    private String description;
    /**
     * 活动奖品列表（包括奖品id，奖品数量以及等级）
     * @NotEmpty 通常作用在容器中
     * @Validated 不能进行嵌套校验
     * @Valid 可以作用在成员属性（字段）中，但是Validated不行
     */
    @NotEmpty(message = "活动奖品列表不能为空")
    @Valid
    private List<CreatePrizeByActivityParam> activityPrizeList;
    /**
     * 参与抽奖活动人员列表
     */
    @NotEmpty(message = "参与抽奖活动人员列表不能为空")
    @Valid
    private List<CreateUserByActivityParam> activityUserList;
}
