package com.community.controller;

import com.community.entity.User;
import com.community.enums.UserType;
import com.community.service.AIService;
import com.community.service.UserService;
import com.community.service.RequestService;
import com.community.service.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * AI 功能控制器
 * 提供 AI 相关功能的 Web 接口
 */
@Controller
@RequestMapping("/ai")
public class AIController {
    
    private static final Logger log = LoggerFactory.getLogger(AIController.class);

    @Autowired
    private AIService aiService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RequestService requestService;
    
    @Autowired
    private ActivityService activityService;

    /**
     * AI 助手页面
     */
    @GetMapping("/assistant")
    public String assistantPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "ai/assistant";
    }

    /**
     * AI 对话接口 - 根据用户角色提供不同服务
     */
    @PostMapping("/chat")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, String> request, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        User user = (User) session.getAttribute("user");
        if (user == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return ResponseEntity.ok(result);
        }

        try {
            String message = request.get("message");
            if (message == null || message.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "消息不能为空");
                return ResponseEntity.ok(result);
            }

            String response;
            Integer userType = user.getUserType();
            
            if (userType != null && userType.equals(UserType.ADMIN.getCode())) {
                // 管理员 - 数据分析功能
                String systemData = getSystemDataForAdmin();
                response = aiService.adminAssistant(message, systemData);
            } else if (userType != null && userType.equals(UserType.VOLUNTEER.getCode())) {
                // 志愿者 - 安全培训和心理辅导
                response = aiService.volunteerAssistant(message);
            } else if (userType != null && userType.equals(UserType.SPECIAL.getCode())) {
                // 特殊人群 - 健康科普
                response = aiService.specialUserAssistant(message);
            } else {
                // 默认客服
                response = aiService.customerService(message);
            }
            
            result.put("success", true);
            result.put("response", response);
        } catch (Exception e) {
            log.error("AI 对话异常", e);
            result.put("success", false);
            result.put("message", "AI 服务异常，请稍后重试");
        }

        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取系统数据供管理员分析
     */
    private String getSystemDataForAdmin() {
        StringBuilder data = new StringBuilder();
        try {
            long specialCount = userService.countByUserType(UserType.SPECIAL.getCode());
            long volunteerCount = userService.countByUserType(UserType.VOLUNTEER.getCode());
            long pendingCount = userService.countPendingUsers();
            long totalRequests = requestService.countAll();
            
            data.append("特殊人群数量：").append(specialCount).append("人\n");
            data.append("志愿者数量：").append(volunteerCount).append("人\n");
            data.append("待审核用户：").append(pendingCount).append("人\n");
            data.append("服务需求总数：").append(totalRequests).append("条\n");
        } catch (Exception e) {
            data.append("数据获取中...");
        }
        return data.toString();
    }

    /**
     * 生成活动描述
     */
    @PostMapping("/generate/activity-description")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateActivityDescription(
            @RequestBody Map<String, String> request, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        User user = (User) session.getAttribute("user");
        if (user == null || user.getUserType() != 3) {
            result.put("success", false);
            result.put("message", "无权限");
            return ResponseEntity.ok(result);
        }

        try {
            String title = request.get("title");
            String location = request.get("location");
            String target = request.get("target");

            if (title == null || location == null) {
                result.put("success", false);
                result.put("message", "参数不完整");
                return ResponseEntity.ok(result);
            }

            String description = aiService.generateActivityDescription(title, location, target);
            result.put("success", true);
            result.put("description", description);
        } catch (Exception e) {
            log.error("生成活动描述异常", e);
            result.put("success", false);
            result.put("message", "生成失败，请稍后重试");
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 生成评价回复
     */
    @PostMapping("/generate/reply")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateReply(
            @RequestBody Map<String, String> request, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        User user = (User) session.getAttribute("user");
        if (user == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return ResponseEntity.ok(result);
        }

        try {
            String evaluation = request.get("evaluation");
            if (evaluation == null || evaluation.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "评价内容不能为空");
                return ResponseEntity.ok(result);
            }

            String reply = aiService.generateReply(evaluation);
            result.put("success", true);
            result.put("reply", reply);
        } catch (Exception e) {
            log.error("生成回复异常", e);
            result.put("success", false);
            result.put("message", "生成失败，请稍后重试");
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 分析紧急求助
     */
    @PostMapping("/analyze/emergency")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> analyzeEmergency(
            @RequestBody Map<String, String> request, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        User user = (User) session.getAttribute("user");
        if (user == null || user.getUserType() != 3) {
            result.put("success", false);
            result.put("message", "无权限");
            return ResponseEntity.ok(result);
        }

        try {
            String emergencyInfo = request.get("emergencyInfo");
            if (emergencyInfo == null || emergencyInfo.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "求助信息不能为空");
                return ResponseEntity.ok(result);
            }

            String analysis = aiService.analyzeEmergency(emergencyInfo);
            result.put("success", true);
            result.put("analysis", analysis);
        } catch (Exception e) {
            log.error("分析紧急求助异常", e);
            result.put("success", false);
            result.put("message", "分析失败，请稍后重试");
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 智能匹配建议
     */
    @PostMapping("/match/skills")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> matchSkills(
            @RequestBody Map<String, String> request, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        User user = (User) session.getAttribute("user");
        if (user == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return ResponseEntity.ok(result);
        }

        try {
            String volunteerSkills = request.get("volunteerSkills");
            String requestNeeds = request.get("requestNeeds");

            if (volunteerSkills == null || requestNeeds == null) {
                result.put("success", false);
                result.put("message", "参数不完整");
                return ResponseEntity.ok(result);
            }

            String matchResult = aiService.matchVolunteerSkills(volunteerSkills, requestNeeds);
            result.put("success", true);
            result.put("matchResult", matchResult);
        } catch (Exception e) {
            log.error("技能匹配异常", e);
            result.put("success", false);
            result.put("message", "匹配失败，请稍后重试");
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 生成数据报告
     */
    @PostMapping("/generate/report")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateReport(
            @RequestBody Map<String, String> request, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        User user = (User) session.getAttribute("user");
        if (user == null || user.getUserType() != 3) {
            result.put("success", false);
            result.put("message", "无权限");
            return ResponseEntity.ok(result);
        }

        try {
            String dataSummary = request.get("dataSummary");
            if (dataSummary == null || dataSummary.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "数据摘要不能为空");
                return ResponseEntity.ok(result);
            }

            String report = aiService.generateDataReport(dataSummary);
            result.put("success", true);
            result.put("report", report);
        } catch (Exception e) {
            log.error("生成报告异常", e);
            result.put("success", false);
            result.put("message", "生成失败，请稍后重试");
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 智能推荐活动
     */
    @PostMapping("/recommend/activities")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> recommendActivities(
            @RequestBody Map<String, String> request, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        User user = (User) session.getAttribute("user");
        if (user == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return ResponseEntity.ok(result);
        }

        try {
            String userProfile = request.get("userProfile");
            String availableActivities = request.get("availableActivities");

            if (userProfile == null || availableActivities == null) {
                result.put("success", false);
                result.put("message", "参数不完整");
                return ResponseEntity.ok(result);
            }

            String recommendation = aiService.recommendActivities(userProfile, availableActivities);
            result.put("success", true);
            result.put("recommendation", recommendation);
        } catch (Exception e) {
            log.error("活动推荐异常", e);
            result.put("success", false);
            result.put("message", "推荐失败，请稍后重试");
        }

        return ResponseEntity.ok(result);
    }
}
