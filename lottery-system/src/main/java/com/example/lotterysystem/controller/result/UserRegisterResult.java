package com.example.lotterysystem.controller.result;

import lombok.Data;

import java.io.Serializable;

/*
所有的返回结果都放在result中
* */
@Data
public class UserRegisterResult implements Serializable {
    /*
    用户id
    * */
    private Long userId;

}
