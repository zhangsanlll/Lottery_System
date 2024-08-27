package com.example.lotterysystem;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.logging.Logger;

@SpringBootTest
public class LogTest {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(LogTest.class);

    @Test
    void testLog(){
        System.out.println("hello bit");
        logger.info("hello.bit");
    }
}
