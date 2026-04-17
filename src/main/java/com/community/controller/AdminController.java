package com.community.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.entity.User;
import com.community.enums.RequestStatus;
import com.community.enums.UserType;
import com.community.service.RequestService;
import com.community.service.UserService;

@Controller
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RequestService requestService;
    
    @GetMapping("/admin/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!checkAdmin(session)) {
            return "redirect:/login";
        }
        
        long specialCount = userService.countByUserType(UserType.SPECIAL.getCode());
        long volunteerCount = userService.countByUserType(UserType.VOLUNTEER.getCode());
        long pendingCount = userService.countPendingUsers();
        // 统计非取消状态的服务需求（排除已取消的）
        long totalRequests = requestService.countActive();
        
        // 按状态统计服务需求
        long pendingRequests = requestService.countByStatus(RequestStatus.PENDING.getCode());
        long acceptedRequests = requestService.countByStatus(RequestStatus.ACCEPTED.getCode());
        long inProgressRequests = requestService.countByStatus(RequestStatus.IN_PROGRESS.getCode());
        long completedRequests = requestService.countByStatus(RequestStatus.COMPLETED.getCode());
        
        // 待处理 = 待接单 + 已接单
        long todoRequests = pendingRequests + acceptedRequests;
        // 进行中 = 服务中
        long doingRequests = inProgressRequests;
        // 已完成 = 已完成
        long doneRequests = completedRequests;
        
        model.addAttribute("specialCount", specialCount);
        model.addAttribute("volunteerCount", volunteerCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("totalRequests", totalRequests);
        model.addAttribute("todoRequests", todoRequests);
        model.addAttribute("doingRequests", doingRequests);
        model.addAttribute("doneRequests", doneRequests);
        
        return "admin/dashboard";
    }
    
    @GetMapping("/admin/user/pending")
    public String pendingUsers(HttpSession session, Model model) {
        if (!checkAdmin(session)) {
            return "redirect:/login";
        }
        List<User> pendingVolunteers = userService.getPendingVolunteers();
        List<User> pendingSpecialUsers = userService.getPendingSpecialUsers();
        model.addAttribute("pendingVolunteers", pendingVolunteers);
        model.addAttribute("pendingSpecialUsers", pendingSpecialUsers);
        return "admin/user_pending";
    }
    
    @PostMapping("/admin/user/approve")
    @ResponseBody
    public Map<String, Object> approveUser(@RequestParam Long userId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        if (!checkAdmin(session)) {
            result.put("success", false);
            result.put("message", "无权限");
            return result;
        }
        try {
            userService.approveUser(userId);
            result.put("success", true);
            result.put("message", "审核通过");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/admin/user/reject")
    @ResponseBody
    public Map<String, Object> rejectUser(@RequestParam Long userId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        if (!checkAdmin(session)) {
            result.put("success", false);
            result.put("message", "无权限");
            return result;
        }
        try {
            userService.rejectUser(userId);
            result.put("success", true);
            result.put("message", "已拒绝");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/admin/user/disable")
    @ResponseBody
    public Map<String, Object> disableUser(@RequestParam Long userId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        if (!checkAdmin(session)) {
            result.put("success", false);
            result.put("message", "无权限");
            return result;
        }
        try {
            userService.disableUser(userId);
            result.put("success", true);
            result.put("message", "已禁用");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/admin/user/enable")
    @ResponseBody
    public Map<String, Object> enableUser(@RequestParam Long userId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        if (!checkAdmin(session)) {
            result.put("success", false);
            result.put("message", "无权限");
            return result;
        }
        try {
            userService.enableUser(userId);
            result.put("success", true);
            result.put("message", "已启用");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/admin/volunteer/list")
    public String volunteerList(HttpSession session, Model model,
                                @RequestParam(defaultValue = "1") int pageNum,
                                @RequestParam(defaultValue = "10") int pageSize) {
        if (!checkAdmin(session)) {
            return "redirect:/login";
        }
        Page<User> page = userService.getVolunteersPage(pageNum, pageSize);
        model.addAttribute("page", page);
        return "admin/volunteer_list";
    }
    
    @GetMapping("/admin/special/list")
    public String specialList(HttpSession session, Model model,
                              @RequestParam(defaultValue = "1") int pageNum,
                              @RequestParam(defaultValue = "10") int pageSize) {
        if (!checkAdmin(session)) {
            return "redirect:/login";
        }
        Page<User> page = userService.getSpecialUsersPage(pageNum, pageSize);
        model.addAttribute("page", page);
        return "admin/special_list";
    }
    
    @GetMapping("/admin/create")
    public String createAdminPage(HttpSession session, Model model) {
        if (!checkAdmin(session)) {
            return "redirect:/login";
        }
        return "admin/admin_create";
    }
    
    @PostMapping("/admin/create")
    @ResponseBody
    public Map<String, Object> createAdmin(User user, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        if (!checkAdmin(session)) {
            result.put("success", false);
            result.put("message", "无权限");
            return result;
        }
        try {
            userService.createAdmin(user);
            result.put("success", true);
            result.put("message", "管理员创建成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    private boolean checkAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && user.getUserType().equals(UserType.ADMIN.getCode());
    }
}
