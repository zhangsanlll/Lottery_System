package com.example.lotterysystem.dao.dataobject;

import com.example.lotterysystem.dao.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ActivityPrizeDO extends BaseDO {
    /**
     * 关联的活动id
     */
    private Long activityId;

    /**
     * 关联的奖品id
     */
    private Long prizeId;
    /**
     * 奖品等级
     */
    private String prizeTiers;
    /**
     * 奖品数量
     */
    private Long prizeAmount;

    /**
     * 活动状态
     */
    private String status;
}
