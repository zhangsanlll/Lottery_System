package com.example.lotterysystem.controller.result;

import com.example.lotterysystem.controller.param.UserPasswordLoginParam;
import lombok.Data;

import java.io.Serializable;


@Data
public class UserLoginResult implements Serializable {
    /**
     * 响应的data数据中包含token和identity两个参数
     */

    /**
     * JWT令牌
     */
    private String token;
    /**
     * 身份信息
     */
    private String identity;
}

