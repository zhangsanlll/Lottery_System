package com.example.lotterysystem.service;


import com.example.lotterysystem.controller.param.UserRegisterParam;
import com.example.lotterysystem.service.dto.UserRegisterDTO;
import org.springframework.stereotype.Service;

public interface UserService {
    //static UserRegisterDTO register(UserRegisterParam param) ;

    /*用户注册
     * */
     UserRegisterDTO register(UserRegisterParam request);
}
