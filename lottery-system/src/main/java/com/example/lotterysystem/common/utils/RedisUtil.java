package com.example.lotterysystem.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * 封装edis的一些方法，使得我们在其他地方使用的时候不会那么麻烦
 */

@Configuration
public class RedisUtil {
    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    /**
     * StringRedisTemplate:直接存放的就是String
     * RedisTemplate:先将存储的数据换成字节数组（不可读），再存储到redis中，读取的时候按字节数组读取出来
     * 在这个项目中我们只需要String，String键值对，所以直接使用StringRedisTemplate
     */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 设置没有过期时间的值
     * @param key
     * @param value
     * @return
     */
    public boolean set(String key,String value){
        try{
            stringRedisTemplate.opsForValue().set(key,value);
            return true;
        }catch (Exception e){
            logger.error("RedisUtil error,set:{},{}",key,value,e);
            return false;
        }
    }


    /**
     * 设置有过期时间的值
     * @param key
     * @param value
     * @param time 单位：秒
     * @return
     */
    public boolean set(String key,String value,Long time){
        try{
            stringRedisTemplate.opsForValue().set(key,value,time, TimeUnit.SECONDS);
            return true;
        }catch (Exception e){
            logger.error("RedisUtil error,set:{},{},{}",key,value,time,e);
            return false;
        }
    }

    /**
     * 获取值
     * @param key
     * @return
     */
    public String get(String key){
        try{
            return StringUtils.hasText(key)?
                    stringRedisTemplate.opsForValue().get(key)
                    :null;

        }catch (Exception e){
            logger.error("RedisUtil error,get:{}",key,e);
            return null;
        }
    }

    /**
     * 删除值，这里我们考虑删除多条值的情况，分类讨论就行
     * @param key ...代表有多个key
     * @return
     */
    public boolean del(String... key){
        try{
            if(null != key && key.length > 0 ){
                if(key.length == 1){
                    stringRedisTemplate.delete(key[0]);
                }else{
                    //先将这多个key封装成List再删除
                    stringRedisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
                }
            }
            return true;
        }catch (Exception e){
            logger.error("RedisUtil error,del:{}",key,e);
            return false;
        }
    }

    /**
     * 判断是否有值
     * @param key
     * @return
     */
    public boolean hasKey(String key){
        try{
            return StringUtils.hasText(key)? stringRedisTemplate.hasKey(key):false;
        }catch (Exception e){
            logger.error("RedisUtil error,hasKey:{}",key,e);
            return false;
        }
    }
}
