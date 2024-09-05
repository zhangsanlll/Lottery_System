package com.example.lotterysystem.controller;

import com.example.lotterysystem.controller.param.ShortMessageLoginParam;
import com.example.lotterysystem.controller.param.UserPasswordLoginParam;
import com.example.lotterysystem.controller.param.UserRegisterParam;
import com.example.lotterysystem.controller.result.BaseUserInfoResult;
import com.example.lotterysystem.controller.result.UserLoginResult;
import com.example.lotterysystem.controller.result.UserRegisterResult;
import com.example.lotterysystem.common.errorcode.ControllerErrorCodeConstants;
import com.example.lotterysystem.common.exception.ControllerException;
import com.example.lotterysystem.common.pojo.CommonResult;
import com.example.lotterysystem.common.utils.JacksonUtil;
import com.example.lotterysystem.service.UserService;
import com.example.lotterysystem.service.VerificationCodeService;
import com.example.lotterysystem.service.dto.UserDTO;
import com.example.lotterysystem.service.dto.UserLoginDTO;
import com.example.lotterysystem.service.dto.UserRegisterDTO;
import com.example.lotterysystem.service.enums.UserIdentityEnum;
import com.example.lotterysystem.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private VerificationCodeService verificationCodeService;
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


    /**
     * 发送验证码
     * @param phoneNumber
     * @return
     */
    @RequestMapping("/verification-code/send")
    public CommonResult<Boolean> sendVerificationCode(String phoneNumber){
        logger.info("sendVerificationCode phoneNumber:{}",phoneNumber);
        verificationCodeService.sendVerificationCode(phoneNumber);
        return CommonResult.success(Boolean.TRUE);
    }


    /**
     * 密码登录
     * @param  param
     * @return
     */
    @RequestMapping("/password/login")
    public CommonResult<UserLoginResult> userPasswordLogin(
            @RequestBody @Validated UserPasswordLoginParam param){
        logger.info("userPasswordLogin  UserPasswordLoginParam:{}",JacksonUtil.writeValueAsString(param));
        UserLoginDTO userLoginDTO = userService.login(param);
        return CommonResult.success(convertToUserLoginResult(userLoginDTO));
        }


    /**
     * 短信验证码登录
     * @param param
     * @return
     */
    @RequestMapping("/message/login")
    public CommonResult<UserLoginResult> shortMessageLogin(
            @RequestBody @Validated ShortMessageLoginParam param){
        logger.info("shortMessageLogin  ShortMessageLoginParam:{}",JacksonUtil.writeValueAsString(param));
        UserLoginDTO userLoginDTO = userService.login(param);
        return CommonResult.success(convertToUserLoginResult(userLoginDTO));
    }
    private UserLoginResult convertToUserLoginResult(UserLoginDTO userLoginDTO) {
        if(null == userLoginDTO){
            throw  new ControllerException(ControllerErrorCodeConstants.Login_FAIL);
        }
        //登录后返回的结果参数
        UserLoginResult userLoginResult = new UserLoginResult();
        userLoginResult.setToken(userLoginDTO.getToken());
        userLoginResult.setIdentity(userLoginDTO.getIdentity().name());
        return userLoginResult;
    }

    /**
     * 根据身份信息查询人员列表
     * @param identity
     * @return
     */
    @RequestMapping("/base-user/find-list")
    public CommonResult<List<BaseUserInfoResult>> findBaseUserInfo (String identity){
        logger.info("findBaseUserInfo identity:{}",identity);
        List<UserDTO> userDTOList = userService.findUserInfo(UserIdentityEnum.forName(identity));
        return CommonResult.success(convertToList(userDTOList));
    }

    private List<BaseUserInfoResult> convertToList(List<UserDTO> userDTOList) {
        if (CollectionUtils.isEmpty(userDTOList)) {
            return Arrays.asList();
        }

        return userDTOList.stream().map(userDTO ->{
            BaseUserInfoResult result = new BaseUserInfoResult();
            result.setUserId(userDTO.getUserId());
            result.setUserName(userDTO.getUserName());
            result.setIdentity(userDTO.getIdentity().name());
            return result;
        }).collect(Collectors.toList());
    }

}

