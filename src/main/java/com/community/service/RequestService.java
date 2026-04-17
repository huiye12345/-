package com.community.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.community.entity.PointsRecord;
import com.community.entity.Request;
import com.community.enums.RequestStatus;
import com.community.mapper.PointsRecordMapper;
import com.community.mapper.RequestMapper;

@Service
public class RequestService {
    
    @Autowired
    private RequestMapper requestMapper;
    
    @Autowired
    private PointsRecordMapper pointsRecordMapper;
    
    @Autowired
    private UserService userService;
    
    public Request createRequest(Request request) {
        request.setStatus(RequestStatus.PENDING.getCode());
        request.setCreateTime(LocalDateTime.now());
        request.setUpdateTime(LocalDateTime.now());
        requestMapper.insert(request);
        return request;
    }
    
    public List<Request> getPendingRequests() {
        QueryWrapper<Request> wrapper = new QueryWrapper<>();
        wrapper.eq("status", RequestStatus.PENDING.getCode());
        wrapper.orderByDesc("create_time");
        return requestMapper.selectList(wrapper);
    }
    
    public List<Request> getUserRequests(Long userId) {
        QueryWrapper<Request> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        return requestMapper.selectList(wrapper);
    }
    
    public List<Request> getVolunteerRequests(Long volunteerId) {
        QueryWrapper<Request> wrapper = new QueryWrapper<>();
        wrapper.eq("volunteer_id", volunteerId);
        wrapper.orderByDesc("create_time");
        return requestMapper.selectList(wrapper);
    }
    
    @Transactional
    public void acceptRequest(Long requestId, Long volunteerId) {
        Request request = requestMapper.selectById(requestId);
        if (request == null) {
            throw new RuntimeException("需求不存在");
        }
        if (!request.getStatus().equals(RequestStatus.PENDING.getCode())) {
            throw new RuntimeException("该需求已被接单");
        }
        request.setVolunteerId(volunteerId);
        request.setStatus(RequestStatus.ACCEPTED.getCode());
        request.setAcceptTime(LocalDateTime.now());
        request.setUpdateTime(LocalDateTime.now());
        requestMapper.updateById(request);
    }
    
    @Transactional
    public void startService(Long requestId, Long volunteerId) {
        Request request = requestMapper.selectById(requestId);
        if (request == null) {
            throw new RuntimeException("需求不存在");
        }
        if (!request.getVolunteerId().equals(volunteerId)) {
            throw new RuntimeException("无权操作");
        }
        request.setStatus(RequestStatus.IN_PROGRESS.getCode());
        request.setUpdateTime(LocalDateTime.now());
        requestMapper.updateById(request);
    }
    
    @Transactional
    public void completeRequest(Long requestId, Long volunteerId) {
        Request request = requestMapper.selectById(requestId);
        if (request == null) {
            throw new RuntimeException("需求不存在");
        }
        if (!request.getVolunteerId().equals(volunteerId)) {
            throw new RuntimeException("无权操作");
        }
        
        request.setStatus(RequestStatus.COMPLETED.getCode());
        request.setCompleteTime(LocalDateTime.now());
        request.setUpdateTime(LocalDateTime.now());
        
        int duration = (int) ChronoUnit.MINUTES.between(request.getAcceptTime(), LocalDateTime.now());
        request.setDuration(duration);
        
        requestMapper.updateById(request);
        
        int hours = duration / 60;
        if (hours < 1) hours = 1;
        
        userService.updateVolunteerHours(volunteerId, hours);
        
        int points = hours * 10;
        userService.updatePoints(volunteerId, points);
        
        PointsRecord record = new PointsRecord();
        record.setUserId(volunteerId);
        record.setPoints(points);
        record.setType(1);
        record.setDescription("完成志愿服务");
        record.setRequestId(requestId);
        record.setCreateTime(LocalDateTime.now());
        pointsRecordMapper.insert(record);
    }
    
    @Transactional
    public void cancelRequest(Long requestId, Long userId) {
        Request request = requestMapper.selectById(requestId);
        if (request == null) {
            throw new RuntimeException("需求不存在");
        }
        if (!request.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }
        request.setStatus(RequestStatus.CANCELLED.getCode());
        request.setUpdateTime(LocalDateTime.now());
        requestMapper.updateById(request);
    }
    
    public Request getRequestById(Long id) {
        return requestMapper.selectById(id);
    }
    
    public long countByStatus(Integer status) {
        QueryWrapper<Request> wrapper = new QueryWrapper<>();
        wrapper.eq("status", status);
        return requestMapper.selectCount(wrapper);
    }
    
    public long countAll() {
        return requestMapper.selectCount(null);
    }
    
    /**
     * 统计非取消状态的服务需求数量（用于数据看板）
     * 排除已取消(status=4)的需求
     */
    public long countActive() {
        QueryWrapper<Request> wrapper = new QueryWrapper<>();
        wrapper.ne("status", RequestStatus.CANCELLED.getCode());
        return requestMapper.selectCount(wrapper);
    }
}
