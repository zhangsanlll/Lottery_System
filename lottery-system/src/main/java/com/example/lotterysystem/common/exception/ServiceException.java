package com.example.lotterysystem.common.exception;
import com.example.lotterysystem.common.errorcode.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends RuntimeException{
    //业务异常码
    /*
    @see package com.example.lotterysystem.common.errorcode.ServiceErrorCodeConstants
    * */
    private  Integer code;

    //错误信息
    private  String message;

    public ServiceException(){

    }

    public ServiceException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ServiceException(ErrorCode errorCode){
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
    }
}
