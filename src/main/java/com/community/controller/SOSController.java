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

import com.community.entity.SOSEmergency;
import com.community.entity.User;
import com.community.enums.UserType;
import com.community.service.SOSEmergencyService;

@Controller
public class SOSController {
    
    @Autowired
    private SOSEmergencyService sosEmergencyService;
    
    @GetMapping("/special/sos")
    public String sosPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getUserType().equals(UserType.SPECIAL.getCode())) {
            return "redirect:/login";
        }
        List<SOSEmergency> emergencies = sosEmergencyService.getUserEmergencies(user.getId());
        model.addAttribute("sosList", emergencies);
        return "special/sos";
    }
    
    @PostMapping("/special/sos/trigger")
    @ResponseBody
    public Map<String, Object> triggerSOS(@RequestParam String location, 
                                           @RequestParam(required = false) String description,
                                           HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new RuntimeException("请先登录");
            }
            SOSEmergency emergency = sosEmergencyService.createEmergency(user.getId(), location, description);
            result.put("success", true);
            result.put("message", "SOS求助已发送，志愿者和管理员已收到通知");
            result.put("emergencyId", emergency.getId());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/volunteer/sos")
    public String volunteerSOSPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getUserType().equals(UserType.VOLUNTEER.getCode())) {
            return "redirect:/login";
        }
        List<SOSEmergency> pendingSOS = sosEmergencyService.getPendingEmergencies();
        List<SOSEmergency> mySOS = sosEmergencyService.getResponderEmergencies(user.getId());
        model.addAttribute("pendingSOS", pendingSOS);
        model.addAttribute("mySOS", mySOS);
        return "volunteer/sos";
    }
    
    @PostMapping("/volunteer/sos/respond")
    @ResponseBody
    public Map<String, Object> respondSOS(@RequestParam Long emergencyId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new RuntimeException("请先登录");
            }
            sosEmergencyService.respondEmergency(emergencyId, user.getId());
            result.put("success", true);
            result.put("message", "您已成功响应该紧急求助，请尽快前往救援");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/volunteer/sos/complete")
    @ResponseBody
    public Map<String, Object> completeSOS(@RequestParam Long emergencyId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new RuntimeException("请先登录");
            }
            sosEmergencyService.resolveEmergency(emergencyId);
            result.put("success", true);
            result.put("message", "救援已完成");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/admin/sos")
    public String adminSOSPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getUserType().equals(UserType.ADMIN.getCode())) {
            return "redirect:/login";
        }
        List<SOSEmergency> pendingSOS = sosEmergencyService.getPendingEmergencies();
        List<SOSEmergency> processingSOS = sosEmergencyService.getProcessingEmergencies();
        List<SOSEmergency> resolvedSOS = sosEmergencyService.getResolvedEmergencies();
        model.addAttribute("pendingSOS", pendingSOS);
        model.addAttribute("processingSOS", processingSOS);
        model.addAttribute("resolvedSOS", resolvedSOS);
        return "admin/sos";
    }
}
