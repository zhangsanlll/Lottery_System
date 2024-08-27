package com.example.lotterysystem.common.errorcode;

import javax.management.loading.MLetContent;

/*
获取不到的错误码，定义为全局错误码
* */
public interface GlobalErrorCodeConstants {

    ErrorCode SUCCESS = new ErrorCode(200,"请求成功");

    ErrorCode INTERNAL_SERVER_ERROR = new ErrorCode(500,"系统异常");
    ErrorCode UNKNOWN = new ErrorCode(900,"未知错误");
}
