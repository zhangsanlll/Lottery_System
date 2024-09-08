package com.example.lotterysystem.dao.mapper;

import com.example.lotterysystem.dao.dataobject.ActivityDO;
import com.example.lotterysystem.dao.dataobject.ActivityPrizeDO;
import com.example.lotterysystem.dao.dataobject.ActivityUserDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ActivityPrizeMapper {
    @Insert("<script> insert into activity_prize (activity_id,prize_id,prize_amount,status,prize_tiers) " +
            "values <foreach collection = 'items' item = 'item' index = 'index' separator = ','>" +
            " (#{item.activityId},#{item.prizeId},#{item.prizeAmount},#{item.status},#{item.prizeTiers})"+
            "</foreach>"+
            "</script>")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int batchInsert(@Param("items")List<ActivityPrizeDO> activityPrizeDOList );


    @Select("select * from activity_prize where activity_id = #{activityId}")
    List<ActivityPrizeDO> selectByActivityId(@Param("activityId") Long activityId);
}

