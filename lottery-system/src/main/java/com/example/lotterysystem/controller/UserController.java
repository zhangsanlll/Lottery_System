package com.example.lotterysystem.controller;

import com.example.lotterysystem.controller.param.UserRegisterParam;
import com.example.lotterysystem.controller.result.UserRegisterResult;
import com.example.lotterysystem.common.errorcode.ControllerErrorCodeConstants;
import com.example.lotterysystem.common.exception.ControllerException;
import com.example.lotterysystem.common.pojo.CommonResult;
import com.example.lotterysystem.common.utils.JacksonUtil;
import com.example.lotterysystem.service.UserService;
import com.example.lotterysystem.service.dto.UserRegisterDTO;
import com.example.lotterysystem.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
所有关于用户和人员的接口,接受前端请求的入口
* */
@RestController
@Slf4j
public class UserController {
    //调用打印日志的相关类
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
//    @Autowired
//    private UserServiceImpl userServiceImpl;
    /*注册

    //从前端接受回来的是Jackson格式，我们需要使用注解@RequestBody将接收到的格式转换成Java对象

     */

    @RequestMapping("/register")
    //@PostMapping
    public CommonResult<UserRegisterResult> userRegister(@Validated @RequestBody UserRegisterParam param){
            //打印日志，好调试
            //将前端的Jackson格式转换成Java并打印日志

            logger.info("userRegisterResultCommonResult UserRegisterParam:{}",
                    JacksonUtil.writeValueAsString(param));
            //调用Service层服务进行访问
            UserRegisterDTO userRegisterDTO = userService.register(param);
            //直接用接口 是这么用的 而不是UserService.register 用类名去调用 是用变量名去调用
            //UserRegisterDTO userRegisterDTO = UserService.register(param);

            return CommonResult.success(convertToRegisterResult(userRegisterDTO));
    }

    //转换参数格式
    private UserRegisterResult convertToRegisterResult(UserRegisterDTO userRegisterDTO) {
        if(null == userRegisterDTO ){
            throw new ControllerException(ControllerErrorCodeConstants.REGISTER_FAIL);
        }
        UserRegisterResult result = new UserRegisterResult();
        result.setUserId(userRegisterDTO.getUserId());
        return result;
    }
}
