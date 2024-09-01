package com.example.lotterysystem.service.impl;

import com.example.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.example.lotterysystem.common.exception.ServiceException;
import com.example.lotterysystem.common.utils.*;
import com.example.lotterysystem.service.VerificationCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private static final String  VERIFICATION_CODE_PREFIX = "verification_code_";
    private static final String VERIFICATION_CODE_TEMPLATE_CODE = "SMS_472470255";
    private static final Long VERIFICATION_CODE_TIMEOUT = 60L;
    @Autowired
    private SMSUtil smsUtil;
    @Autowired
    private RedisUtil redisUtil;
    /**
     * 发送验证码
      * @param phoneNumber
     */
    @Override
    public void sendVerificationCode(String phoneNumber) {
        //验证手机号
        if(!RegexUtil.checkMobile(phoneNumber)){
            throw new ServiceException(ServiceErrorCodeConstants.PHONE_NUMBER_ERROR);
        }
        //生成随机验证码,调用hutool工具中我们自定义的验证码工具类
        String code = CaptchaUtil.getCaptcha(4);

        //发送验证码
        //{"code":XXXX}
        Map<String ,String> map = new HashMap<>();
        map.put("code",code);
        smsUtil.sendMessage(VERIFICATION_CODE_TEMPLATE_CODE,phoneNumber,
                JacksonUtil.writeValueAsString(map));
        //缓存验证码
        //131xxxxxxxx:code
        redisUtil.set(VERIFICATION_CODE_PREFIX+phoneNumber,code,VERIFICATION_CODE_TIMEOUT);
    }

    /**
     * 接收验证码
     * @param phoneNumber
     * @return
     */
    @Override
    public String getVerificationCode(String phoneNumber) {
        //验证手机号
        if(!RegexUtil.checkMobile(phoneNumber)){
            throw new ServiceException(ServiceErrorCodeConstants.PHONE_NUMBER_ERROR);
        }

        return redisUtil.get(VERIFICATION_CODE_PREFIX+phoneNumber);
    }
}
