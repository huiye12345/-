package com.community.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.community.entity.Activity;
import com.community.entity.ActivityRecord;
import com.community.entity.PointsRecord;
import com.community.mapper.ActivityMapper;
import com.community.mapper.ActivityRecordMapper;
import com.community.mapper.PointsRecordMapper;

@Service
public class ActivityService {
    
    @Autowired
    private ActivityMapper activityMapper;
    
    @Autowired
    private ActivityRecordMapper activityRecordMapper;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PointsRecordMapper pointsRecordMapper;
    
    // ==================== 管理员功能 ====================
    
    public Activity createActivity(Activity activity) {
        activity.setCurrentParticipants(0);
        activity.setStatus(0);
        activity.setCreateTime(LocalDateTime.now());
        activity.setUpdateTime(LocalDateTime.now());
        activityMapper.insert(activity);
        return activity;
    }
    
    public Activity updateActivity(Activity activity) {
        activity.setUpdateTime(LocalDateTime.now());
        activityMapper.updateById(activity);
        return activity;
    }
    
    public void deleteActivity(Long activityId) {
        activityMapper.deleteById(activityId);
    }
    
    public Activity getActivityById(Long id) {
        return activityMapper.selectById(id);
    }
    
    public List<Activity> getAllActivities() {
        QueryWrapper<Activity> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        return activityMapper.selectList(wrapper);
    }
    
    public List<Activity> getActiveActivities() {
        return activityMapper.selectActiveActivities();
    }
    
    public List<Activity> getActivitiesByStatus(Integer status) {
        return activityMapper.selectByStatus(status);
    }
    
    @Transactional
    public void startActivity(Long activityId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        if (activity.getStatus() != 0 && activity.getStatus() != 1) {
            throw new RuntimeException("活动状态不允许开始");
        }
        activity.setStatus(2);
        activity.setUpdateTime(LocalDateTime.now());
        activityMapper.updateById(activity);
    }
    
    @Transactional
    public void endActivity(Long activityId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        if (activity.getStatus() != 2) {
            throw new RuntimeException("活动不在进行中状态");
        }
        activity.setStatus(3);
        activity.setUpdateTime(LocalDateTime.now());
        activityMapper.updateById(activity);
    }
    
    // ==================== 志愿者功能 ====================
    
    @Transactional
    public void joinActivity(Long activityId, Long userId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        
        if (activity.getStatus() != 0 && activity.getStatus() != 1) {
            throw new RuntimeException("活动不在报名阶段");
        }
        
        // 检查是否已报名
        ActivityRecord existRecord = activityRecordMapper.selectByActivityAndUser(activityId, userId);
        if (existRecord != null && existRecord.getStatus() != 3) {
            throw new RuntimeException("您已报名该活动");
        }
        
        // 检查人数限制
        if (activity.getMaxParticipants() > 0 && 
            activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new RuntimeException("活动名额已满");
        }
        
        // 创建报名记录
        ActivityRecord record = new ActivityRecord();
        record.setActivityId(activityId);
        record.setUserId(userId);
        record.setStatus(0);
        record.setCreateTime(LocalDateTime.now());
        activityRecordMapper.insert(record);
        
        // 更新活动参与人数
        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        activityMapper.updateById(activity);
    }
    
    @Transactional
    public void cancelJoin(Long activityId, Long userId) {
        ActivityRecord record = activityRecordMapper.selectByActivityAndUser(activityId, userId);
        if (record == null) {
            throw new RuntimeException("您未报名该活动");
        }
        
        if (record.getStatus() == 2) {
            throw new RuntimeException("活动已完成，无法取消");
        }
        
        record.setStatus(3);
        activityRecordMapper.updateById(record);
        
        // 更新活动参与人数
        Activity activity = activityMapper.selectById(activityId);
        if (activity != null && activity.getCurrentParticipants() > 0) {
            activity.setCurrentParticipants(activity.getCurrentParticipants() - 1);
            activityMapper.updateById(activity);
        }
    }
    
    @Transactional
    public void signInActivity(Long activityId, Long userId) {
        ActivityRecord record = activityRecordMapper.selectByActivityAndUser(activityId, userId);
        if (record == null) {
            throw new RuntimeException("您未报名该活动");
        }
        
        if (record.getStatus() != 0) {
            throw new RuntimeException("当前状态无法签到");
        }
        
        record.setStatus(1);
        record.setSignInTime(LocalDateTime.now());
        activityRecordMapper.updateById(record);
    }
    
    @Transactional
    public void completeActivity(Long activityId, Long userId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        
        ActivityRecord record = activityRecordMapper.selectByActivityAndUser(activityId, userId);
        if (record == null) {
            throw new RuntimeException("您未报名该活动");
        }
        
        if (record.getStatus() != 1) {
            throw new RuntimeException("请先签到后再完成活动");
        }
        
        // 更新报名记录
        record.setStatus(2);
        record.setCompleteTime(LocalDateTime.now());
        record.setPointsEarned(activity.getPoints());
        activityRecordMapper.updateById(record);
        
        // 更新用户积分
        userService.updatePoints(userId, activity.getPoints());
        
        // 更新用户服务时长
        if (activity.getHours() != null && activity.getHours() > 0) {
            userService.updateVolunteerHours(userId, activity.getHours());
        }
        
        // 添加积分记录
        PointsRecord pointsRecord = new PointsRecord();
        pointsRecord.setUserId(userId);
        pointsRecord.setPoints(activity.getPoints());
        pointsRecord.setType(1);
        pointsRecord.setDescription("完成公益活动：" + activity.getTitle());
        pointsRecord.setCreateTime(LocalDateTime.now());
        pointsRecordMapper.insert(pointsRecord);
    }
    
    public List<ActivityRecord> getUserActivityRecords(Long userId) {
        return activityRecordMapper.selectByUserId(userId);
    }
    
    public List<ActivityRecord> getActivityParticipants(Long activityId) {
        return activityRecordMapper.selectByActivityId(activityId);
    }
    
    public ActivityRecord getUserActivityRecord(Long activityId, Long userId) {
        return activityRecordMapper.selectByActivityAndUser(activityId, userId);
    }
}
