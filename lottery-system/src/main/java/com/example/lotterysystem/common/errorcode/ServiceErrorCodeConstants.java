package com.example.lotterysystem.common.errorcode;

public interface ServiceErrorCodeConstants {
    //人员模块错误码
    ErrorCode REGISTER_INFO_IS_EMPTY = new ErrorCode(100,"参数为空");
    ErrorCode MAIL_ERROR = new ErrorCode(101,"邮箱错误");
    ErrorCode PHONE_NUMBER_ERROR = new ErrorCode(102,"手机号错误");

    ErrorCode IDENTITY_ERROR = new ErrorCode(103,"身份信息错误");
    ErrorCode PASSWORD_ERROR = new ErrorCode(104,"密码错误");
    ErrorCode PASSWORD_FORMAT_ERROR = new ErrorCode(105,"密码至少6位！");
    ErrorCode MAIL_USED = new ErrorCode(106,"邮箱被使用过");
    ErrorCode MAIL_IS_EMPTY = new ErrorCode(107,"邮箱为空");
    ErrorCode PHONE_USED = new ErrorCode(108,"手机号为空");
    ErrorCode PHONE_IS_EMPTY = new ErrorCode(109,"手机号杯使用过");


    //活动模块错误码


    //奖品模块错误码


    //抽奖模块错误码
}
