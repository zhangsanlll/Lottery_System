package com.example.lotterysystem;

import com.example.lotterysystem.service.impl.VerificationCodeServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class VerificationCodeTest {

    @Autowired
    private VerificationCodeServiceImpl verificationCodeService;

    @Test
    void verificationTest(){
        verificationCodeService.sendVerificationCode("18481017932");
        System.out.println(verificationCodeService.getVerificationCode("18481017932"));
    }
}
