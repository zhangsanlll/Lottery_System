package com.example.lotterysystem.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ActivityPrizeTiersStatusEnum {
    FIRST_PRIZE(1,"一等奖"),
    SECOND_PRIZE(2,"二等奖"),
    THIRD_PRIZE(3,"三等奖");

    private final Integer code;
    private final String message;

    public static ActivityPrizeTiersStatusEnum forName(String name) {
        //遍历该枚举变量，与接收参数的身份信息进行对比，如果相等就返回message
        for(ActivityPrizeTiersStatusEnum activityPrizeTiersStatusEnum : ActivityPrizeTiersStatusEnum.values()){
            if(activityPrizeTiersStatusEnum.name().equalsIgnoreCase(name)){
                return activityPrizeTiersStatusEnum;
            }
        }
        return null;
    }
}
