package com.example.lotterysystem.common.exception;

import com.example.lotterysystem.common.errorcode.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/*
controller层异常类
* * */
@Data
@EqualsAndHashCode(callSuper = true)

public class ControllerException extends RuntimeException{
    //业务异常码
    /*
    @see package com.example.lotterysystem.common.errorcode.ControllerErrorCodeConstants
    * */
    private  Integer code;

    //错误信息
    private  String message;

    //为了序列化加上一个无参构造函数
    public ControllerException(){

    }

    public ControllerException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ControllerException(ErrorCode errorCode){
        this.code = errorCode.getCode();
        this.message = errorCode.getMeg();
    }
}
