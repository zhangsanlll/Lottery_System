package com.example.lotterysystem;

import com.example.lotterysystem.dao.dataobject.Encrypt;
import com.example.lotterysystem.dao.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SqlTest {

    @Autowired
    private UserMapper userMapper;
    @Test
    void countByMail(){
        int mailCount = userMapper.countByMail("12345@qq.com");
        System.out.println(mailCount);
    }

    @Test
    void countByPhone(){
        int PhoneCount = userMapper.countByPhone(new Encrypt("13455768232"));
        System.out.println(PhoneCount);
    }

}
