package com.example.lotterysystem;

import com.example.lotterysystem.common.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class RedisTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Test
    void redisTest(){
        stringRedisTemplate.opsForValue().set("key","value");
        System.out.println(stringRedisTemplate.opsForValue().get("key"));
    }

    @Test
    void redisUtil(){
        redisUtil.set("key1","value1");
        redisUtil.set("key2","value2",60L);
        System.out.println(redisUtil.get("key1"));
        System.out.println(redisUtil.get("key2"));
        System.out.println(redisUtil.hasKey("key1"));
        System.out.println(redisUtil.hasKey("key2"));
        System.out.println(redisUtil.del("key1"));
        System.out.println(redisUtil.hasKey("key1"));
        System.out.println(redisUtil.hasKey("key2"));
    }
}
