package com.community.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import com.community.entity.Request;
import com.community.entity.User;
import com.community.enums.UserType;
import com.community.service.RequestService;

@Controller
public class RequestController {
    
    @Autowired
    private RequestService requestService;
    
    @GetMapping("/special/request")
    public String myRequests(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getUserType().equals(UserType.SPECIAL.getCode())) {
            return "redirect:/login";
        }
        List<Request> requests = requestService.getUserRequests(user.getId());
        model.addAttribute("requests", requests);
        return "special/request";
    }
    
    @GetMapping("/special/request/create")
    public String createRequestPage() {
        return "special/request_create";
    }
    
    @PostMapping("/special/request/create")
    @ResponseBody
    public Map<String, Object> createRequest(
            @RequestParam String title,
            @RequestParam String serviceType,
            @RequestParam String content,
            @RequestParam String address,
            @RequestParam String serviceTime,
            HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new RuntimeException("请先登录");
            }
            Request request = new Request();
            request.setUserId(user.getId());
            request.setTitle(title);
            request.setServiceType(serviceType);
            request.setContent(content);
            request.setAddress(address);
            // 解析日期时间格式: 支持 2026-04-12 09:13 或 2026-04-12T09:13
            LocalDateTime serviceDateTime;
            if (serviceTime.contains("T")) {
                serviceDateTime = LocalDateTime.parse(serviceTime);
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                serviceDateTime = LocalDateTime.parse(serviceTime, formatter);
            }
            request.setServiceTime(serviceDateTime);
            requestService.createRequest(request);
            result.put("success", true);
            result.put("message", "需求发布成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/special/request/cancel")
    @ResponseBody
    public Map<String, Object> cancelRequest(@RequestParam Long requestId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new RuntimeException("请先登录");
            }
            requestService.cancelRequest(requestId, user.getId());
            result.put("success", true);
            result.put("message", "需求已取消");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/volunteer/request")
    public String volunteerRequests(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getUserType().equals(UserType.VOLUNTEER.getCode())) {
            return "redirect:/login";
        }
        List<Request> pendingRequests = requestService.getPendingRequests();
        List<Request> myRequests = requestService.getVolunteerRequests(user.getId());
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("myRequests", myRequests);
        return "volunteer/request";
    }
    
    @PostMapping("/volunteer/request/accept")
    @ResponseBody
    public Map<String, Object> acceptRequest(@RequestParam Long requestId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new RuntimeException("请先登录");
            }
            requestService.acceptRequest(requestId, user.getId());
            result.put("success", true);
            result.put("message", "接单成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/volunteer/request/start")
    @ResponseBody
    public Map<String, Object> startService(@RequestParam Long requestId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new RuntimeException("请先登录");
            }
            requestService.startService(requestId, user.getId());
            result.put("success", true);
            result.put("message", "服务已开始");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/volunteer/request/complete")
    @ResponseBody
    public Map<String, Object> completeRequest(@RequestParam Long requestId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new RuntimeException("请先登录");
            }
            requestService.completeRequest(requestId, user.getId());
            result.put("success", true);
            result.put("message", "服务完成，已获得积分和时长");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
