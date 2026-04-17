package com.community.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.entity.SOSEmergency;

@Mapper
public interface SOSEmergencyMapper extends BaseMapper<SOSEmergency> {
    
    List<SOSEmergency> selectPendingEmergencies();
    
    List<SOSEmergency> selectByUserId(@Param("userId") Long userId);
}
