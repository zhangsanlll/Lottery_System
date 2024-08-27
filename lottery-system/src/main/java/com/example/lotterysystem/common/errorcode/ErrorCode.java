package com.example.lotterysystem.common.errorcode;

import lombok.Data;

@Data
public class ErrorCode {
    //错误码
    private final Integer code;

    //错误信息
    private final String meg;

    public ErrorCode(Integer code, String meg) {
        this.code = code;
        this.meg = meg;
    }
}
