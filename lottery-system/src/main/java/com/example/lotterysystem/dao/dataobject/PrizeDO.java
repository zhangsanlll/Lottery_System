package com.example.lotterysystem.dao.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class PrizeDO extends BaseDO{
    /**
     * 奖品名称
     */
    private String name;
    /**
     * 奖品图片
     */
    private String imageUrl;//需要跟数据库中的名称保持一致，有下划线的转成驼峰
    /**
     * 价格
     */
    private BigDecimal price;
    /**
     * 描述
     */
    private String description;
}
