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
    ErrorCode PHONE_USED = new ErrorCode(108,"手机号杯使用过");
    ErrorCode PHONE_IS_EMPTY = new ErrorCode(109,"手机号为空");

    ErrorCode LOGIN_INFO_NOT_EXIT = new ErrorCode(110,"登录信息不存在");
    ErrorCode USER_INFO_IS_EMPTY = new ErrorCode(111,"用户信息为空");
    ErrorCode VERIFICATION_CODE_ERROR = new ErrorCode(112,"验证码错误");




    //奖品模块错误码



    //活动模块错误码
    ErrorCode CREATE_ACTIVITY_INFO_IS_EMPTY = new ErrorCode(300,"创建活动信息为空");
    ErrorCode ACTIVITY_USER_ERROR = new ErrorCode(301,"创建活动关联人员异常");
    ErrorCode ACTIVITY_PRIZE_ERROR = new ErrorCode(302,"创建活动关联奖品异常");
    ErrorCode USER_PRIZE_AMOUNT_ERROR = new ErrorCode(303,"创建活动关联的奖品数量和人员数量异常");
    ErrorCode ACTIVITY_PRIZE_TIERS_ERROR = new ErrorCode(304,"活动奖品等级错误");
    ErrorCode ACTIVITY_STATUS_CONVERT_FAIL = new ErrorCode(305,"活动状态转换失败！");
    ErrorCode CACHE_ACTIVITY_ID_IS_EMPTY = new ErrorCode(306,"缓存的活动id为空！！");
    ErrorCode CACHE_ACTIVITY_ID_ERROR= new ErrorCode(307,"缓存的活动id错误！");



    //抽奖模块错误码
    ErrorCode ACTIVITY_OR_PRIZE_IS_EMPTY = new ErrorCode(400,"抽奖活动或关联奖品不存在");
    ErrorCode ACTIVITY_COMPLETED = new ErrorCode(401,"抽奖活动已完成，无法抽奖");
    ErrorCode ACTIVITY_PRIZE_COMPLETED =new ErrorCode(402,"当前奖品已被抽取，无法抽奖");
    ErrorCode WINNER_PRIZE_AMOUNT_ERROR =new ErrorCode(403,"中奖人数与奖品数量不一致，无法抽奖");




    //图片错误码
    ErrorCode PIC_UPLOAD_ERROR = new ErrorCode(500,"上传图片错误");


}
