package com.community.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.entity.Request;

@Mapper
public interface RequestMapper extends BaseMapper<Request> {
    
    List<Request> selectPendingRequests();
    
    List<Request> selectByUserId(@Param("userId") Long userId);
    
    List<Request> selectByVolunteerId(@Param("volunteerId") Long volunteerId);
}
