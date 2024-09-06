package com.example.lotterysystem.dao.mapper;

import com.example.lotterysystem.dao.dataobject.PrizeDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PrizeMapper {

    @Insert("insert into prize (name,image_url,price,description) " +
            " values ( #{name},#{imageUrl},#{price},#{description})")
    //需要返回id
    @Options(useGeneratedKeys = true,keyColumn = "id",keyProperty = "id")
    int insert(PrizeDO prizeDO);

    @Select("select count(1) from prize")
    int count();

    @Select("select * from prize order by id desc limit #{offset},#{pageSize}")
    List<PrizeDO> selectPrizeList(@Param("offset") Integer offset,
                                  @Param("pageSize")Integer pageSize);
}
