package com.community.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.entity.User;
import com.community.enums.UserType;
import com.community.mapper.UserMapper;
import com.community.util.PasswordEncoder;

@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    public User register(User user) {
        User existUser = userMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setStatus(0);
        user.setVolunteerHours(0);
        user.setPoints(0);
        user.setLevel(1);
        // 启用密码加密：对新注册用户进行密码加密
        user.setPassword(PasswordEncoder.encode(user.getPassword()));
        userMapper.insert(user);
        return user;
    }
    
    public User login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 启用密码加密验证：兼容明文密码和加密密码
        boolean passwordMatch;
        if (PasswordEncoder.isEncoded(user.getPassword())) {
            // 密码已加密，使用PasswordEncoder验证
            passwordMatch = PasswordEncoder.matches(password, user.getPassword());
        } else {
            // 密码未加密（旧数据），使用明文比较
            passwordMatch = user.getPassword().equals(password);
        }
        if (!passwordMatch) {
            throw new RuntimeException("密码错误");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号待审核");
        }
        if (user.getStatus() == 2) {
            throw new RuntimeException("账号已被禁用");
        }
        return user;
    }
    
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }
    
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }
    
    public User updateUser(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return user;
    }
    
    public List<User> getPendingUsers() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        return userMapper.selectList(wrapper);
    }
    
    public Page<User> getPendingUsersPage(int pageNum, int pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        return userMapper.selectPage(page, wrapper);
    }
    
    public List<User> getPendingVolunteers() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        wrapper.eq("user_type", 2);
        wrapper.orderByDesc("create_time");
        return userMapper.selectList(wrapper);
    }
    
    public List<User> getPendingSpecialUsers() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        wrapper.eq("user_type", 1);
        wrapper.orderByDesc("create_time");
        return userMapper.selectList(wrapper);
    }
    
    public void approveUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setStatus(1);
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }
    
    public void rejectUser(Long userId) {
        userMapper.deleteById(userId);
    }
    
    public void disableUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setStatus(2);
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }

    public void enableUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setStatus(1);
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }
    
    public List<User> getVolunteers() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_type", 2);
        wrapper.eq("status", 1);
        return userMapper.selectList(wrapper);
    }
    
    public Page<User> getVolunteersPage(int pageNum, int pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_type", 2);
        wrapper.ne("status", 0);
        wrapper.orderByDesc("level");
        wrapper.orderByDesc("volunteer_hours");
        return userMapper.selectPage(page, wrapper);
    }
    
    public void updateVolunteerHours(Long userId, int hours) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setVolunteerHours(user.getVolunteerHours() + hours);
            int newLevel = calculateLevel(user.getVolunteerHours());
            user.setLevel(newLevel);
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }
    
    public void updatePoints(Long userId, int points) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPoints(user.getPoints() + points);
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }
    
    public int calculateLevel(int hours) {
        if (hours < 50) return 1;
        if (hours < 100) return 2;
        if (hours < 200) return 3;
        if (hours < 500) return 4;
        if (hours < 1000) return 5;
        return 5 + hours / 500;
    }
    
    public List<User> getSpecialUsers() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_type", 1);
        wrapper.eq("status", 1);
        return userMapper.selectList(wrapper);
    }
    
    public Page<User> getSpecialUsersPage(int pageNum, int pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_type", 1);
        wrapper.ne("status", 0);
        return userMapper.selectPage(page, wrapper);
    }
    
    public long countByUserType(Integer userType) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_type", userType);
        wrapper.eq("status", 1);
        return userMapper.selectCount(wrapper);
    }
    
    public long countPendingUsers() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        return userMapper.selectCount(wrapper);
    }
    
    public User createAdmin(User user) {
        User existUser = userMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        user.setUserType(UserType.ADMIN.getCode());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setVolunteerHours(0);
        user.setPoints(0);
        user.setLevel(1);
        userMapper.insert(user);
        return user;
    }
}
