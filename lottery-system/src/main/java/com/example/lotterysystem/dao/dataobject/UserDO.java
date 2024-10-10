package com.example.lotterysystem.dao.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserDO extends BaseDO{
    private String userName;
    private String email;
    private String identity;
    //这里没有用Encrypt的原因是密码要单独使用哈希算法进行加密
    private String password;
    private Encrypt phoneNumber;
}
