package com.example.lotterysystem.controller.handler;

import com.example.lotterysystem.common.errorcode.GlobalErrorCodeConstants;
import com.example.lotterysystem.common.exception.ControllerException;
import com.example.lotterysystem.common.exception.ServiceException;
import com.example.lotterysystem.common.pojo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//@Slf4j
@RestControllerAdvice //捕获全局抛出的异常
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = ServiceException.class)
    public CommonResult<?> serviceException (ServiceException e){
        //打印错误日志,不用占位符
        logger.error("serviceException:",e);
        //log.error("serviceException:",e);
        //构造错误结果
        return CommonResult.error(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode(),e.getMessage());
    }

    @ExceptionHandler(value = ControllerException.class)
    public CommonResult<?> controllerException (ControllerException e){
        //打印错误日志,不用占位符
        logger.error("controllerException:",e);
        //log.error("serviceException:",e);
        //构造错误结果
        return CommonResult.error(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode(),e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public CommonResult<?> exception (Exception e){
        //打印错误日志,不用占位符
        logger.error("服务异常:",e);
        //log.error("serviceException:",e);
        //构造错误结果
        return CommonResult.error(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode(),e.getMessage());
    }
}
