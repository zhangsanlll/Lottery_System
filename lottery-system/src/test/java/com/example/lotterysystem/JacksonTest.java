package com.example.lotterysystem;

import com.example.lotterysystem.common.pojo.CommonResult;
import com.example.lotterysystem.common.utils.JacksonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/*
序列化测试
 */
@SpringBootTest
public class JacksonTest {

    @Test
    void jacksonObjectTest(){
        //Jackson库中用来解析Jackson的一个类，里面有很多方法
        ObjectMapper objectMapper = new ObjectMapper();
        //统一返回异常码
        //把异常码序列化返回
        CommonResult<String> result = CommonResult.error(500,"系统错误");
        String str;

        //序列化

        try {
            str = objectMapper.writeValueAsString(result);
            System.out.println(str);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        //反序列化
        try {
            CommonResult<String> readResult = objectMapper.readValue(str,CommonResult.class);
            System.out.println(readResult.getCode()+readResult.getMsg());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        //List序列化
        List<CommonResult<String>> commonResults = Arrays.asList(
                CommonResult.success("success1"),
                CommonResult.success("success2")
        );
        try {
            str = objectMapper.writeValueAsString(commonResults);
            System.out.println(str);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        //List反序列
        JavaType javaType = objectMapper.getTypeFactory().
                constructParametricType(List.class,CommonResult.class);
        try {
            commonResults = objectMapper.readValue(str,javaType);
            for(CommonResult<String> commonResult : commonResults){
                System.out.println(commonResult.getData());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void jacksonUtilsTest(){
        CommonResult<String > commonResult = CommonResult.success("success");
        String str;

        //序列化
        str = JacksonUtil.writeValueAsString(commonResult);
        System.out.println(str);

        //反序列化
        commonResult = JacksonUtil.readValue(str,CommonResult.class);
        System.out.println(commonResult.getData());

        System.out.println("====================");


        //List序列化
        List<CommonResult<String>> commonResults = Arrays.asList(
                CommonResult.success("success1"),
                CommonResult.success("success2")
        );
        str = JacksonUtil.writeValueAsString(commonResults);
        System.out.println(str);

        //List反序列化
        commonResults = JacksonUtil.readListValue(str,CommonResult.class);
        for(CommonResult<String> result : commonResults){
            System.out.println(result.getData());
        }
    }


}
