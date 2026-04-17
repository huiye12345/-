package com.community.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.community.entity.Activity;
import com.community.entity.ActivityRecord;
import com.community.entity.User;
import com.community.service.ActivityService;

@Controller
@RequestMapping("/activity")
public class ActivityController {
    
    @Autowired
    private ActivityService activityService;
    
    // ==================== 管理员功能 ====================
    
    @GetMapping("/admin/list")
    public String adminActivityList(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getUserType() != 3) {
            return "redirect:/login";
        }
        
        List<Activity> activities = activityService.getAllActivities();
        model.addAttribute("activities", activities);
        return "admin/activity_list";
    }
    
    @GetMapping("/admin/create")
    public String adminCreateActivityPage(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getUserType() != 3) {
            return "redirect:/login";
        }
        return "admin/activity_create";
    }
    
    @PostMapping("/admin/create")
    @ResponseBody
    public Map<String, Object> createActivity(@RequestBody Activity activity, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null || user.getUserType() != 3) {
                result.put("success", false);
                result.put("message", "无权操作");
                return result;
            }
            
            activityService.createActivity(activity);
            result.put("success", true);
            result.put("message", "活动发布成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/admin/update")
    @ResponseBody
    public Map<String, Object> updateActivity(@RequestBody Activity activity, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null || user.getUserType() != 3) {
                result.put("success", false);
                result.put("message", "无权操作");
                return result;
            }
            
            activityService.updateActivity(activity);
            result.put("success", true);
            result.put("message", "活动更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/admin/delete/{id}")
    @ResponseBody
    public Map<String, Object> deleteActivity(@PathVariable Long id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null || user.getUserType() != 3) {
                result.put("success", false);
                result.put("message", "无权操作");
                return result;
            }
            
            activityService.deleteActivity(id);
            result.put("success", true);
            result.put("message", "活动删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/admin/participants/{activityId}")
    public String activityParticipants(@PathVariable Long activityId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getUserType() != 3) {
            return "redirect:/login";
        }
        
        Activity activity = activityService.getActivityById(activityId);
        List<ActivityRecord> participants = activityService.getActivityParticipants(activityId);
        
        model.addAttribute("activity", activity);
        model.addAttribute("participants", participants);
        return "admin/activity_participants";
    }
    
    @PostMapping("/admin/start/{activityId}")
    @ResponseBody
    public Map<String, Object> startActivity(@PathVariable Long activityId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null || user.getUserType() != 3) {
                result.put("success", false);
                result.put("message", "无权操作");
                return result;
            }
            
            activityService.startActivity(activityId);
            result.put("success", true);
            result.put("message", "活动已开始，志愿者可以签到");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/admin/end/{activityId}")
    @ResponseBody
    public Map<String, Object> endActivity(@PathVariable Long activityId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null || user.getUserType() != 3) {
                result.put("success", false);
                result.put("message", "无权操作");
                return result;
            }
            
            activityService.endActivity(activityId);
            result.put("success", true);
            result.put("message", "活动已结束");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    // ==================== 志愿者功能 ====================
    
    @GetMapping("/list")
    public String activityList(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        List<Activity> activities = activityService.getActiveActivities();
        model.addAttribute("activities", activities);
        model.addAttribute("user", user);
        return "volunteer/activity_list";
    }
    
    @GetMapping("/my")
    public String myActivities(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        List<ActivityRecord> records = activityService.getUserActivityRecords(user.getId());
        model.addAttribute("records", records);
        model.addAttribute("user", user);
        return "volunteer/activity_my";
    }
    
    @PostMapping("/join/{activityId}")
    @ResponseBody
    public Map<String, Object> joinActivity(@PathVariable Long activityId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }
            
            if (user.getUserType() != 2) {
                result.put("success", false);
                result.put("message", "只有志愿者可以报名活动");
                return result;
            }
            
            activityService.joinActivity(activityId, user.getId());
            result.put("success", true);
            result.put("message", "报名成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/cancel/{activityId}")
    @ResponseBody
    public Map<String, Object> cancelActivity(@PathVariable Long activityId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }
            
            activityService.cancelJoin(activityId, user.getId());
            result.put("success", true);
            result.put("message", "取消报名成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/signin/{activityId}")
    @ResponseBody
    public Map<String, Object> signInActivity(@PathVariable Long activityId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }
            
            activityService.signInActivity(activityId, user.getId());
            result.put("success", true);
            result.put("message", "签到成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/complete/{activityId}")
    @ResponseBody
    public Map<String, Object> completeActivity(@PathVariable Long activityId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }
            
            activityService.completeActivity(activityId, user.getId());
            result.put("success", true);
            result.put("message", "活动完成，积分已发放");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/detail/{id}")
    public String activityDetail(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        Activity activity = activityService.getActivityById(id);
        ActivityRecord record = activityService.getUserActivityRecord(id, user.getId());
        
        model.addAttribute("activity", activity);
        model.addAttribute("record", record);
        model.addAttribute("user", user);
        return "volunteer/activity_detail";
    }
}
