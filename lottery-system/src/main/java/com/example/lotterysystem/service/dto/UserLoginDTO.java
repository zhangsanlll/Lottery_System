package com.example.lotterysystem.service.dto;

import com.example.lotterysystem.service.enums.UserIdentityEnum;
import lombok.Data;

@Data
/**
 * 放回的数据中包含的这两个方法
 */
public class UserLoginDTO {
    /**
     * 令牌
     */
    private String token;

    /**
     * 登录人员身份
     */
    private UserIdentityEnum identity;
}
