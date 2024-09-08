package com.example.lotterysystem.common.errorcode;

public interface ControllerErrorCodeConstants {
    //人员模块错误码,1开头
    ErrorCode REGISTER_FAIL = new ErrorCode(100,"注册错误！");
    ErrorCode Login_FAIL = new ErrorCode(101,"登录失败！");

    //奖品模块错误码
    ErrorCode FIND_PRIZE_LIST_ERROR = new ErrorCode(200,"登录失败！");


    //活动模块错误码
    ErrorCode CREATE_ACTIVITY_ERROR = new ErrorCode(300,"创建活动失败！");
    ErrorCode FIND_ACTIVITY_LIST_ERROR = new ErrorCode(301,"查找活动列表失败！");
    ErrorCode GET_ACTIVITY_DETAIL_ERROR = new ErrorCode(302,"查找活动详细信息失败！");




    //抽奖模块错误码


}
