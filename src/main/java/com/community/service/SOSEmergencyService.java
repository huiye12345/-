package com.community.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.community.entity.SOSEmergency;
import com.community.mapper.SOSEmergencyMapper;

@Service
public class SOSEmergencyService {
    
    @Autowired
    private SOSEmergencyMapper sosEmergencyMapper;
    
    public SOSEmergency createEmergency(Long userId, String location, String description) {
        SOSEmergency emergency = new SOSEmergency();
        emergency.setUserId(userId);
        emergency.setLocation(location);
        emergency.setDescription(description);
        emergency.setStatus(0);
        emergency.setCreateTime(LocalDateTime.now());
        sosEmergencyMapper.insert(emergency);
        return emergency;
    }
    
    public List<SOSEmergency> getPendingEmergencies() {
        QueryWrapper<SOSEmergency> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        wrapper.orderByDesc("create_time");
        return sosEmergencyMapper.selectList(wrapper);
    }
    
    public List<SOSEmergency> getUserEmergencies(Long userId) {
        QueryWrapper<SOSEmergency> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        return sosEmergencyMapper.selectList(wrapper);
    }
    
    public List<SOSEmergency> getResponderEmergencies(Long responderId) {
        QueryWrapper<SOSEmergency> wrapper = new QueryWrapper<>();
        wrapper.eq("responder_id", responderId);
        wrapper.orderByDesc("create_time");
        return sosEmergencyMapper.selectList(wrapper);
    }
    
    public void respondEmergency(Long emergencyId, Long responderId) {
        SOSEmergency emergency = sosEmergencyMapper.selectById(emergencyId);
        if (emergency != null) {
            emergency.setResponderId(responderId);
            emergency.setStatus(1);
            emergency.setRespondTime(LocalDateTime.now());
            sosEmergencyMapper.updateById(emergency);
        }
    }
    
    public void resolveEmergency(Long emergencyId) {
        SOSEmergency emergency = sosEmergencyMapper.selectById(emergencyId);
        if (emergency != null) {
            emergency.setStatus(2);
            emergency.setResolveTime(LocalDateTime.now());
            sosEmergencyMapper.updateById(emergency);
        }
    }
    
    public long countPendingEmergencies() {
        QueryWrapper<SOSEmergency> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        return sosEmergencyMapper.selectCount(wrapper);
    }

    public List<SOSEmergency> getProcessingEmergencies() {
        QueryWrapper<SOSEmergency> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.orderByDesc("create_time");
        return sosEmergencyMapper.selectList(wrapper);
    }

    public List<SOSEmergency> getResolvedEmergencies() {
        QueryWrapper<SOSEmergency> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 2);
        wrapper.orderByDesc("create_time");
        return sosEmergencyMapper.selectList(wrapper);
    }
}
