package com.example.lotterysystem.controller.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/*
controller中注册中所涉及的请求入参都放在这个类中维护调用
* */
@Data
public class UserRegisterParam implements Serializable {
    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "邮箱不能为空")
    private String mail;

    @NotBlank(message = "电话不能为空")
    private String phoneNumber;

    private String password;

    @NotBlank(message = "身份信息不能为空")
    private String identity;
}
