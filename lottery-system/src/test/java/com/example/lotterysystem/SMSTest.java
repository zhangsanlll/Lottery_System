package com.example.lotterysystem;

import com.example.lotterysystem.common.utils.SMSUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SMSTest {
    @Autowired
    private SMSUtil smsUtil;

    @Test
    void smsTest(){
        //${"code":"1344"}
        smsUtil.sendMessage("SMS_472470255",
                "18481017932",
                "{\"code\":\"6437\"}");
    }
}
