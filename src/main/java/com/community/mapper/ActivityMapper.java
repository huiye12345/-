package com.community.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.entity.Activity;

@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {
    
    @Select("SELECT * FROM tb_activity WHERE status = #{status} ORDER BY activity_time ASC")
    java.util.List<Activity> selectByStatus(@Param("status") Integer status);
    
    @Select("SELECT * FROM tb_activity WHERE status IN (0, 1) ORDER BY activity_time ASC")
    java.util.List<Activity> selectActiveActivities();
}
