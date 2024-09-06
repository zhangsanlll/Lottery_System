package com.example.lotterysystem.controller.param;

import com.example.lotterysystem.service.enums.UserIdentityEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserPasswordLoginParam extends UserLoginParam {

    /**
     * 手机或者邮箱
     */
    @NotBlank(message = "手机或者邮箱不为空")
    private String loginName;

    /**
     * 密码
     */
    @NotBlank(message = "密码不为空")
    private String password;
}
