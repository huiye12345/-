package com.community.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.community.entity.Reward;
import com.community.entity.RewardExchange;
import com.community.entity.PointsRecord;
import com.community.mapper.RewardMapper;
import com.community.mapper.RewardExchangeMapper;
import com.community.mapper.PointsRecordMapper;

@Service
public class RewardService {
    
    @Autowired
    private RewardMapper rewardMapper;
    
    @Autowired
    private RewardExchangeMapper rewardExchangeMapper;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PointsRecordMapper pointsRecordMapper;
    
    // ==================== 管理员功能 ====================
    
    public Reward createReward(Reward reward) {
        reward.setStatus(1);
        reward.setCreateTime(LocalDateTime.now());
        rewardMapper.insert(reward);
        return reward;
    }
    
    public Reward updateReward(Reward reward) {
        rewardMapper.updateById(reward);
        return reward;
    }
    
    public void deleteReward(Long rewardId) {
        rewardMapper.deleteById(rewardId);
    }
    
    public Reward getRewardById(Long id) {
        return rewardMapper.selectById(id);
    }
    
    public List<Reward> getAllRewards() {
        QueryWrapper<Reward> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        return rewardMapper.selectList(wrapper);
    }
    
    public List<Reward> getActiveRewards() {
        return rewardMapper.selectActiveRewards();
    }
    
    // ==================== 用户功能 ====================
    
    @Transactional
    public void exchangeReward(Long rewardId, Long userId) {
        Reward reward = rewardMapper.selectById(rewardId);
        if (reward == null) {
            throw new RuntimeException("奖励不存在");
        }
        
        if (reward.getStatus() != 1) {
            throw new RuntimeException("该奖励已下架");
        }
        
        if (reward.getStock() <= 0) {
            throw new RuntimeException("该奖励库存不足");
        }
        
        // 检查用户积分
        com.community.entity.User user = userService.getUserById(userId);
        if (user.getPoints() < reward.getPoints()) {
            throw new RuntimeException("积分不足，需要 " + reward.getPoints() + " 积分");
        }
        
        // 扣除积分
        userService.updatePoints(userId, -reward.getPoints());
        
        // 减少库存
        reward.setStock(reward.getStock() - 1);
        rewardMapper.updateById(reward);
        
        // 创建兑换记录
        RewardExchange exchange = new RewardExchange();
        exchange.setUserId(userId);
        exchange.setRewardId(rewardId);
        exchange.setPoints(reward.getPoints());
        exchange.setStatus(0);
        exchange.setCreateTime(LocalDateTime.now());
        rewardExchangeMapper.insert(exchange);
        
        // 添加积分记录
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPoints(-reward.getPoints());
        record.setType(2);
        record.setDescription("兑换奖励：" + reward.getName());
        record.setCreateTime(LocalDateTime.now());
        pointsRecordMapper.insert(record);
    }
    
    public List<RewardExchange> getUserExchanges(Long userId) {
        List<RewardExchange> exchanges = rewardExchangeMapper.selectByUserId(userId);
        // 填充奖励名称
        for (RewardExchange exchange : exchanges) {
            Reward reward = rewardMapper.selectById(exchange.getRewardId());
            if (reward != null) {
                exchange.setRewardName(reward.getName());
            }
        }
        return exchanges;
    }
    
    // ==================== 管理员兑换管理功能 ====================
    
    public List<RewardExchange> getAllExchanges() {
        List<RewardExchange> exchanges = rewardExchangeMapper.selectList(null);
        // 填充奖励名称
        for (RewardExchange exchange : exchanges) {
            Reward reward = rewardMapper.selectById(exchange.getRewardId());
            if (reward != null) {
                exchange.setRewardName(reward.getName());
            }
        }
        return exchanges;
    }
    
    public List<RewardExchange> getExchangesByStatus(Integer status) {
        QueryWrapper<RewardExchange> wrapper = new QueryWrapper<>();
        wrapper.eq("status", status);
        wrapper.orderByDesc("create_time");
        return rewardExchangeMapper.selectList(wrapper);
    }
    
    @Transactional
    public void processExchange(Long exchangeId, Integer status) {
        RewardExchange exchange = rewardExchangeMapper.selectById(exchangeId);
        if (exchange == null) {
            throw new RuntimeException("兑换记录不存在");
        }
        
        if (exchange.getStatus() != 0) {
            throw new RuntimeException("该兑换已处理");
        }
        
        // 如果拒绝兑换，返还库存和积分
        if (status == 2) {
            // 返还积分
            userService.updatePoints(exchange.getUserId(), exchange.getPoints());
            
            // 返还库存
            Reward reward = rewardMapper.selectById(exchange.getRewardId());
            if (reward != null) {
                reward.setStock(reward.getStock() + 1);
                rewardMapper.updateById(reward);
            }
            
            // 添加积分返还记录
            PointsRecord record = new PointsRecord();
            record.setUserId(exchange.getUserId());
            record.setPoints(exchange.getPoints());
            record.setType(1);
            record.setDescription("兑换被拒绝，返还积分");
            record.setCreateTime(LocalDateTime.now());
            pointsRecordMapper.insert(record);
        }
        
        exchange.setStatus(status);
        rewardExchangeMapper.updateById(exchange);
    }
    
    @Transactional
    public void cancelExchange(Long exchangeId, Long userId) {
        RewardExchange exchange = rewardExchangeMapper.selectById(exchangeId);
        if (exchange == null) {
            throw new RuntimeException("兑换记录不存在");
        }
        
        if (!exchange.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }
        
        if (exchange.getStatus() != 0) {
            throw new RuntimeException("该兑换已处理，无法取消");
        }
        
        // 返还积分
        userService.updatePoints(userId, exchange.getPoints());
        
        // 返还库存
        Reward reward = rewardMapper.selectById(exchange.getRewardId());
        if (reward != null) {
            reward.setStock(reward.getStock() + 1);
            rewardMapper.updateById(reward);
        }
        
        // 更新兑换状态为已取消
        exchange.setStatus(2);
        rewardExchangeMapper.updateById(exchange);
        
        // 添加积分记录
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPoints(exchange.getPoints());
        record.setType(1);
        record.setDescription("取消兑换，返还积分");
        record.setCreateTime(LocalDateTime.now());
        pointsRecordMapper.insert(record);
    }
}
