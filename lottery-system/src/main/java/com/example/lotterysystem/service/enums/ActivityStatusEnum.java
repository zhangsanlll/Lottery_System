package com.example.lotterysystem.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ActivityStatusEnum {
    RUNNING(1,"活动进行中"),
    COMPLETED(2,"活动已完成");

    private final Integer code;
    private final String message;

    public static ActivityStatusEnum forName(String name) {
        //遍历该枚举变量，与接收参数的身份信息进行对比，如果相等就返回message
        for(ActivityStatusEnum activityStatusEnum : ActivityStatusEnum.values()){
            if(activityStatusEnum.name().equalsIgnoreCase(name)){
                return activityStatusEnum;
            }
        }
        return null;
    }
}
