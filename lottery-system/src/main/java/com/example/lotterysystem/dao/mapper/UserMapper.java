package com.example.lotterysystem.dao.mapper;

import com.example.lotterysystem.dao.dataobject.Encrypt;
import com.example.lotterysystem.dao.dataobject.UserDO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("select count(*) from user where email = #{email}")
    int countByMail(@Param("email" ) String mail);


    @Select("select count(*) from user where phone_number = #{phoneNumber}")
    int countByPhone(@Param("phoneNumber" ) Encrypt phoneNumber);

    @Insert("insert into user(user_name,email,phone_number,identity,password)" +
            " values(#{userName},#{email},#{phoneNumber},#{identity},#{password})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")//
    void insert(UserDO userDO);
}
