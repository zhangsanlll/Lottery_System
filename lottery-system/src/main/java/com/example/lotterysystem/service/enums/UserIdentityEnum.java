package com.example.lotterysystem.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor//生成全参构造函数
public enum UserIdentityEnum {
    /*
    两个身份：管理员，普通用户
    * */
    ADMIN("管理员"),
    NORMAL("普通用户");

    private final String message;

    public static UserIdentityEnum forName(String name) {
        //遍历该枚举变量，与接收参数的身份信息进行对比，如果相等就返回message
        for(UserIdentityEnum userIdentityEnum : UserIdentityEnum.values()){
            if(userIdentityEnum.name().equalsIgnoreCase(name)){
                return userIdentityEnum;
            }
        }
        return null;
    }
}
