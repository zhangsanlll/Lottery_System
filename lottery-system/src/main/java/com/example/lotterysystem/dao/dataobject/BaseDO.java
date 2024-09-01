package com.example.lotterysystem.dao.dataobject;

/**
 * 每⼀张表必须都要包含三个字段，主键（id）、创建时间（gmtCreate）、
 * 修改时间（gmtModified），因此这⾥定义⼀个基类对象BaseDO，让每个映射数据
 * 库表的类都继承BaseDO。
 */

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseDO implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后更新时间
     */
    private Date gmtModified;
}
