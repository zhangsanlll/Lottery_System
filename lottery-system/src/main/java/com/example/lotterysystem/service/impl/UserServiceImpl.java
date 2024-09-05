package com.example.lotterysystem.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.example.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.example.lotterysystem.common.exception.ServiceException;
import com.example.lotterysystem.common.utils.JWTUtil;
import com.example.lotterysystem.common.utils.RegexUtil;
import com.example.lotterysystem.controller.UserController;
import com.example.lotterysystem.controller.param.ShortMessageLoginParam;
import com.example.lotterysystem.controller.param.UserLoginParam;
import com.example.lotterysystem.controller.param.UserPasswordLoginParam;
import com.example.lotterysystem.controller.param.UserRegisterParam;
import com.example.lotterysystem.dao.dataobject.Encrypt;
import com.example.lotterysystem.dao.dataobject.UserDO;
import com.example.lotterysystem.dao.mapper.UserMapper;
import com.example.lotterysystem.service.UserService;
import com.example.lotterysystem.service.VerificationCodeService;
import com.example.lotterysystem.service.dto.UserDTO;
import com.example.lotterysystem.service.dto.UserLoginDTO;
import com.example.lotterysystem.service.dto.UserRegisterDTO;
import com.example.lotterysystem.service.enums.UserIdentityEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.sql.rowset.serial.SerialException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Override
    public UserRegisterDTO register(UserRegisterParam request) {
        //校验所有参数
        checkRegisterInfo(request);

        //加密私密数据（构造dao层请求）
        UserDO userDO = new UserDO();
        userDO.setUserName(request.getName());
        userDO.setEmail(request.getMail());
        userDO.setIdentity(request.getIdentity());
        userDO.setPhoneNumber(new Encrypt(request.getPhoneNumber()));
        //密码只有管理员可以设置，但是管理员也可以不设置，所以需要先判断
        if(StringUtils.hasText(request.getPassword())){
            //使用哈希算法对密码进行加密
            userDO.setPassword(DigestUtil.sha256Hex(request.getPassword()));
        }
        //保存数据
        userMapper.insert(userDO);
        //构造返回
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setUserId(userDO.getId());
        return userRegisterDTO;

    }

    /**
     * 登录方法的具体实现
     * @param param 定义成父类，但接收的参数是子类的，进行了 向上转型
     * @return
     */
    @Override
    public UserLoginDTO login(UserLoginParam param) {
        UserLoginDTO userLoginDTO;
        //由于param定义的是父类，所以需要对参数类型进行检查与转换
        //Java14及以上版本中定义了这样的语法，一句就可以搞定
        //这条语句的含义是：如果param的类型是UserPasswordLoginParam ，
        // 那么loginParam就是向上转型后UserPasswordLoginParam的变量名
        if(param instanceof UserPasswordLoginParam loginParam){
            //密码登录流程
            userLoginDTO = loginByUserPassword(loginParam);
        }else if(param instanceof ShortMessageLoginParam loginParam){
            //验证码登录流程
            userLoginDTO = loginByShortMessage(loginParam);
        }else{
            throw new ServiceException(ServiceErrorCodeConstants.LOGIN_INFO_NOT_EXIT);
        }
        return userLoginDTO;
    }

    @Override
    public List<UserDTO> findUserInfo(UserIdentityEnum identity) {
        String identityString = null ==identity ? null :identity.name();
        //查表
        List<UserDO> userDOList = userMapper.selectUserListByIdentity(identityString);
        List<UserDTO> userDTOList = userDOList.stream().map(userDO -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(userDO.getId());
            userDTO.setUserName(userDO.getUserName());
            userDTO.setEmail(userDO.getEmail());
            userDTO.setPhoneNumber(userDO.getPhoneNumber().getValue());
            userDTO.setIdentity(UserIdentityEnum.forName(userDO.getIdentity()));
            return userDTO;
        }).collect(Collectors.toList());
            return userDTOList;
    }

    /**
     * 短信验证码登录
     * @param loginParam
     * @return
     */
    private UserLoginDTO loginByShortMessage(ShortMessageLoginParam loginParam) {

        //校验手机号
        if(!RegexUtil.checkMobile(loginParam.getLoginMobile())){
            throw new ServiceException(ServiceErrorCodeConstants.PHONE_NUMBER_ERROR);
        }
        //获取用户数据
        UserDO userDo = userMapper.selectByMobile(new Encrypt(loginParam.getLoginMobile()));
        if(null == userDo){
            throw new ServiceException(ServiceErrorCodeConstants.USER_INFO_IS_EMPTY);
        } else if (StringUtils.hasText(loginParam.getMandatoryIdentity()) &&
                !loginParam.getMandatoryIdentity().equals(userDo.getIdentity())) {
            //验证是否是强制身份
            throw new ServiceException(ServiceErrorCodeConstants.IDENTITY_ERROR);
        }

        //校验验证码
        String code = verificationCodeService.getVerificationCode(loginParam.getLoginMobile());
        if(! loginParam.getVerificationCode().equals(code)){
            throw new ServiceException(ServiceErrorCodeConstants.VERIFICATION_CODE_ERROR);
        }
        //所有信息校验完了之后开始塞入返回值（JWT)
        Map<String , Object> claim = new HashMap<>();
        claim.put("id",userDo.getId());
        claim.put("identity",userDo.getIdentity());
        String token = JWTUtil.genJwt(claim);

        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setToken(token);
        userLoginDTO.setIdentity(UserIdentityEnum.forName(userDo.getIdentity()));
        return userLoginDTO;
    }

    /**
     * 密码登录
     * @param loginParam
     * @return
     */
    private UserLoginDTO loginByUserPassword(UserPasswordLoginParam loginParam) {
        UserDO userDO;
        //判断手机登录还是邮箱登录
        logger.info("走到这里了,loginParam.getLoginName():{}",loginParam.getLoginName());
        if(RegexUtil.checkMail(loginParam.getLoginName())){
            //邮箱登录
            //根据邮箱查询用户表
            logger.info("邮箱登录");
            userDO = userMapper.selectByMail(loginParam.getLoginName());
        } else if (RegexUtil.checkMobile(loginParam.getLoginName())) {
            //手机号登录
            //根据手机号查询用户表
            //手机号需要加密
            logger.info("手机号登录");
            userDO = userMapper.selectByMobile(new Encrypt(loginParam.getLoginName()));
        }else{
            throw new ServiceException(ServiceErrorCodeConstants.LOGIN_INFO_NOT_EXIT);
        }

        //校验登录信息
        if(null == userDO){
            throw new ServiceException(ServiceErrorCodeConstants.MAIL_IS_EMPTY);
        } else if (StringUtils.hasText(loginParam.getMandatoryIdentity())
        && !loginParam.getMandatoryIdentity().equalsIgnoreCase(userDO.getIdentity())) {
            //强制身份登录，身份校验不通过
            throw new ServiceException(ServiceErrorCodeConstants.IDENTITY_ERROR);
        } else if (!DigestUtil.sha256Hex(loginParam.getPassword()).equals(userDO.getPassword())) {
            //校验密码不同
            throw new ServiceException(ServiceErrorCodeConstants.PASSWORD_ERROR);
        }

        //所有信息校验完了之后开始塞入返回值（JWT)
        Map<String , Object> claim = new HashMap<>();
        claim.put("id",userDO.getId());
        claim.put("identity",userDO.getIdentity());
        String token = JWTUtil.genJwt(claim);

        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setToken(token);
        userLoginDTO.setIdentity(UserIdentityEnum.forName(userDO.getIdentity()));
        return userLoginDTO;
    }

    /*
    校验是否满足注册要求
    * */
    private void checkRegisterInfo(UserRegisterParam param) {
        //首先校验参数是否为空，如果不校验，后面会报一个空指针异常的错误，所以我们自己抛一个异常
        if(null == param){
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_INFO_IS_EMPTY);
        }
        /*
        对以下参数进行校验都采用正则表达式，或者可以使用Hutool中的校验方法
        * */

        //验证手机号格式

        if(!RegexUtil.checkMobile(param.getPhoneNumber())){
            //log.info("param.getPhoneNumber():{}",param.getPhoneNumber());
            boolean ret = RegexUtil.checkMobile(param.getPhoneNumber());
           // log.info("ret:{}",ret);
            throw new ServiceException(ServiceErrorCodeConstants.PHONE_NUMBER_ERROR);
        }

        //校验邮箱格式

        if(!RegexUtil.checkMail(param.getMail())){
            throw new ServiceException(ServiceErrorCodeConstants.MAIL_ERROR);
        }

        //验证身份信息
        if(null == UserIdentityEnum.forName(param.getIdentity())){
            throw new ServiceException(ServiceErrorCodeConstants.IDENTITY_ERROR);
        }
        //管理员必须设置密码
        if(param.getIdentity().equals(UserIdentityEnum.ADMIN.name())
        && !StringUtils.hasText(param.getPassword())){
            throw new ServiceException(ServiceErrorCodeConstants.PASSWORD_ERROR);
        }
        //密码格式验证，最少6位

        if(StringUtils.hasText(param.getPassword())
        && ! RegexUtil.checkPassword(param.getPassword())){
            throw new ServiceException(ServiceErrorCodeConstants.PASSWORD_FORMAT_ERROR);
        }
        //验证邮箱是否被使用过
        if(checkMailUsed(param.getMail())){
            throw new ServiceException(ServiceErrorCodeConstants.MAIL_USED);
        }


        //验证手机号是否被使用过
        if(checkPhoneUsed(param.getPhoneNumber())){
            throw new ServiceException(ServiceErrorCodeConstants.PHONE_USED);
        }
    }

    /*检测邮箱是否被使用过
     * */
    private boolean checkMailUsed(String mail) {
        if(!StringUtils.hasText(mail)){
            throw new ServiceException(ServiceErrorCodeConstants.MAIL_IS_EMPTY);
        }
        return userMapper.countByMail(mail) > 0;
    }

    /**
     * 手机号存入数据库时需要考虑是否加密，因为手机号是隐私数据，不能以明文的方式存入
     *    使用MyBatis中的TypeHandler将手机号加密之后存入数据库中
     *    此时我们TypeHandler指定的类型如果是String的话，会让其他以String存入数据库中的数据也加密存入，但实际上没必要
     *    于是我们将phonenumber的String类型转换一下，只将手机号加密存入即可，不会伤及其他的数据
     */
    private boolean checkPhoneUsed(String phoneNumber) {
        if(!StringUtils.hasText(phoneNumber)){
            throw new ServiceException(ServiceErrorCodeConstants.PHONE_IS_EMPTY);
        }
        //
        return userMapper.countByPhone(new Encrypt(phoneNumber)) > 0;
    }



}
