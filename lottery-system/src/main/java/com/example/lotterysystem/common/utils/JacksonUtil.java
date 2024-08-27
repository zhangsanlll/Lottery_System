package com.example.lotterysystem.common.utils;

import com.fasterxml.jackson.core.JacksonException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.json.JsonParseException;

import java.util.List;
import java.util.concurrent.Callable;

public class JacksonUtil {
    //构造函数
    private JacksonUtil(){

    }
    /*静态代码块单例
    * */
    private final static ObjectMapper OBJECT_MAPPER;
    static {
        OBJECT_MAPPER = new ObjectMapper();
    }

    private static ObjectMapper getObjectMapper(){
        return OBJECT_MAPPER;
    }

    private static <T> T tryParse(Callable<T> parser){
        return tryParse(parser, JacksonException.class);
    }

    //序列化
    private static <T> T tryParse(Callable<T> parser,Class<? extends Exception> check){
        try {
            return parser.call();
        } catch (Exception e) {
            if(check.isAssignableFrom(e.getClass())){
                throw new JsonParseException(e);
            }
            throw new IllegalStateException(e);
        }
    }
    /*反序列化list
    * */
    public static <T> T readListValue(String content,Class<?> parameterClass){
        return JacksonUtil.tryParse(()->
                JacksonUtil.getObjectMapper().readValue(content,
                        JacksonUtil.getObjectMapper().
                                getTypeFactory().
                                constructParametricType(List.class,parameterClass)));

    }

    /*序列化
    * */
    public static String writeValueAsString(Object value){
        return JacksonUtil.tryParse(()->
                JacksonUtil.getObjectMapper().writeValueAsString(value));
    }
}
