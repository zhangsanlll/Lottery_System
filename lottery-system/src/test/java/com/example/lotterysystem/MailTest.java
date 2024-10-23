package com.example.lotterysystem;

import com.example.lotterysystem.common.utils.MailUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MailTest {

    @Autowired
    private MailUtil mailUtil;


    @Test
    void mailTest(){
        mailUtil.sendSampleMail("xxxxxx@qq.com","标题：测试邮件发送服务","xxxxxxxxx");
    }
}
