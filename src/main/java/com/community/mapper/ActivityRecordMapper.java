package com.community.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.entity.ActivityRecord;

@Mapper
public interface ActivityRecordMapper extends BaseMapper<ActivityRecord> {
    
    @Select("SELECT * FROM tb_activity_record WHERE activity_id = #{activityId}")
    java.util.List<ActivityRecord> selectByActivityId(@Param("activityId") Long activityId);
    
    @Select("SELECT * FROM tb_activity_record WHERE user_id = #{userId} ORDER BY create_time DESC")
    java.util.List<ActivityRecord> selectByUserId(@Param("userId") Long userId);
    
    @Select("SELECT * FROM tb_activity_record WHERE activity_id = #{activityId} AND user_id = #{userId}")
    ActivityRecord selectByActivityAndUser(@Param("activityId") Long activityId, @Param("userId") Long userId);
    
    @Select("SELECT COUNT(*) FROM tb_activity_record WHERE activity_id = #{activityId} AND status != 3")
    Integer countParticipants(@Param("activityId") Long activityId);
}
