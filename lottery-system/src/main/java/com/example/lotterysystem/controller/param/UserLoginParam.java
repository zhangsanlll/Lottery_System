package com.example.lotterysystem.controller.param;

import com.example.lotterysystem.service.enums.UserIdentityEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 约定前后端交互接口中发现两种登录方式都有一个身份信息，所以我们直接把它提取出来
 */
@Data
public class UserLoginParam implements Serializable {
    /**
     * 身份信息,强制某身份登录，不填不限制身份
     * @see UserIdentityEnum#name()
     */
    private String  mandatoryIdentity;
}
