package com.example.lotterysystem.common.pojo;

import com.example.lotterysystem.common.errorcode.ErrorCode;
import com.example.lotterysystem.common.errorcode.GlobalErrorCodeConstants;
import lombok.Data;
import org.springframework.util.Assert;

@Data
public class CommonResult<T> {
    //返回错误码
    private Integer code;

    //正常返回数据
    private T data;

    /*错误码描述
    * */
    private String msg;

    //响应成功统一返回数据
    public static <T> CommonResult<T> success(T data){
        CommonResult<T> result = new CommonResult<>();
        result.code = GlobalErrorCodeConstants.SUCCESS.getCode();
        result.data = data;
        result.msg = "";
        return result;
    }

    public static <T> CommonResult<T> error(Integer code,String msg){
        //先断言一下
        Assert.isTrue(!GlobalErrorCodeConstants.SUCCESS.getCode().equals(code),
                "code不是错误的异常");
        //new出一个新对象调用
        CommonResult<T> result = new CommonResult<>();
        result.code = code;
        result.msg = msg;
        return result;
    }


    /**
     * 将传入的result对象，转换成另一个泛型结果的对象
     *     <T> 返回的泛型
     * @param errorCode
     * @return 新的CommonResult对象
     * @param <T>
     */
    public static <T> CommonResult<T> error(ErrorCode errorCode) {
    return error(errorCode.getCode(),errorCode.getMsg());
    }

}
