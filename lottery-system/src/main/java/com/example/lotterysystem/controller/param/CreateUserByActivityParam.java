package com.example.lotterysystem.controller.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class CreateUserByActivityParam implements Serializable {
    /**
     * 人员id
     */
    @NotNull(message = "人员id不能为空 ！")
    private Long userId;
    /**
     * 人员姓名
     */
    @NotBlank(message = "人员姓名不能为空！")
    private String userName;
}
