package com.example.lotterysystem.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ActivityPrizeStatusEnum {
    INIT(1,"初始化"),
    COMPLETED(2,"已被抽取");

    private final Integer code;
    private final String message;

    public static ActivityPrizeStatusEnum forName(String name) {
        //遍历该枚举变量，与接收参数的身份信息进行对比，如果相等就返回message
        for(ActivityPrizeStatusEnum activityPrizeStatusEnum : ActivityPrizeStatusEnum.values()){
            if(activityPrizeStatusEnum.name().equalsIgnoreCase(name)){
                return activityPrizeStatusEnum;
            }
        }
        return null;
    }
}
