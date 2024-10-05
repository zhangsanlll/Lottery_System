package com.example.lotterysystem.controller.result;

import com.example.lotterysystem.service.enums.ActivityPrizeTiersStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class GetActivityDetailResult implements Serializable {
    /**
     * 活动id
     */
    private Long activityId;
    /**
     * 活动名称
     */
    private String activityName;
    /**
     * 活动描述
     */
    private String description;
    /**
     * 活动是否有效
     */
    private Boolean valid;
    /**
     * 获奖信息（列表）
     */
    private List<Prize> prizes;

    /**
     * 获奖信息（人员）
     */
    private List<User> users;

    @Data
    public static class Prize{
        /**
         * 奖品id
         */
        private Long prizeId;
        /**
         * 奖品名
         */
        private String name;
        /**
         * 图片索引
         */
        private String imageUrl;
        /**
         * 价格
         */
        private BigDecimal price;
        /**
         * 描述
         */
        private String description;
        /**
         * 奖品等级
         * @see ActivityPrizeTiersStatusEnum#getMessage()
         */
        private String prizeTierName;
        /**
         * 奖品数量
         */
        private Long prizeAmount;
        /**
         * 奖品是否有效
         */
        private Boolean valid;
    }

    @Data
    public static class User{
        /**
         * 用户id
         */
        private Long userId;
        /**
         * 姓名
         */
        private String userName;
        /**
         * 人员是否被抽取
         */
        private Boolean valid;
    }

}
