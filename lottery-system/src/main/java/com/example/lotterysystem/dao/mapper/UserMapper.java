package com.example.lotterysystem.dao.mapper;

import com.example.lotterysystem.dao.dataobject.Encrypt;
import com.example.lotterysystem.dao.dataobject.UserDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select count(*) from user where email = #{email}")
    int countByMail(@Param("email" ) String email);


    @Select("select count(*) from user where phone_number = #{phoneNumber}")
    int countByPhone(@Param("phoneNumber" ) Encrypt phoneNumber);

    @Insert("insert into user(user_name,email,phone_number,identity,password)" +
            " values(#{userName},#{email},#{phoneNumber},#{identity},#{password})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")//
    void insert(UserDO userDO);


    @Select("select * from user where email = #{email}")
    UserDO selectByMail(@Param("email" )String email);

    @Select("select * from user where phone_number = #{phoneNumber}")
    UserDO selectByMobile(@Param("phoneNumber" )Encrypt phoneNumber);

    /**
     * 根据身份信息查询人员列表
     * @param identity
     * @return
     */
    @Select("<script>" +
            " select * from user" +
            " <if test=\"identity!=null\">" +
            "    where identity = #{identity}" +
            " </if>" +
            " order by id desc" +
            " </script>")
    List<UserDO> selectUserListByIdentity(@Param("identity" )String identity);
}
