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
import org.springframework.web.bind.annotation.ResponseBody;

import com.community.entity.Reward;
import com.community.entity.RewardExchange;
import com.community.entity.User;
import com.community.service.RewardService;

@Controller
@RequestMapping("/reward")
public class RewardController {
    
    @Autowired
    private RewardService rewardService;
    
    // ==================== 管理员功能 ====================
    
    @GetMapping("/admin/list")
    public String adminRewardList(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getUserType() != 3) {
            return "redirect:/login";
        }
        
        List<Reward> rewards = rewardService.getAllRewards();
        model.addAttribute("rewards", rewards);
        return "admin/reward_list";
    }
    
    @GetMapping("/admin/create")
    public String adminCreateRewardPage(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getUserType() != 3) {
            return "redirect:/login";
        }
        return "admin/reward_create";
    }
    
    @GetMapping("/admin/edit/{id}")
    public String adminEditRewardPage(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getUserType() != 3) {
            return "redirect:/login";
        }
        
        Reward reward = rewardService.getRewardById(id);
        if (reward == null) {
            return "redirect:/reward/admin/list";
        }
        
        model.addAttribute("reward", reward);
        return "admin/reward_edit";
    }
    
    @PostMapping("/admin/create")
    @ResponseBody
    public Map<String, Object> createReward(@RequestBody Reward reward, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null || user.getUserType() != 3) {
                result.put("success", false);
                result.put("message", "无权操作");
                return result;
            }
            
            rewardService.createReward(reward);
            result.put("success", true);
            result.put("message", "奖励添加成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/admin/update")
    @ResponseBody
    public Map<String, Object> updateReward(@RequestBody Reward reward, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null || user.getUserType() != 3) {
                result.put("success", false);
                result.put("message", "无权操作");
                return result;
            }
            
            rewardService.updateReward(reward);
            result.put("success", true);
            result.put("message", "奖励更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/admin/delete/{id}")
    @ResponseBody
    public Map<String, Object> deleteReward(@PathVariable Long id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null || user.getUserType() != 3) {
                result.put("success", false);
                result.put("message", "无权操作");
                return result;
            }
            
            rewardService.deleteReward(id);
            result.put("success", true);
            result.put("message", "奖励删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    // ==================== 用户功能 ====================
    
    @GetMapping("/list")
    public String rewardList(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        List<Reward> rewards = rewardService.getActiveRewards();
        model.addAttribute("rewards", rewards);
        model.addAttribute("user", user);
        return "volunteer/reward_list";
    }
    
    @GetMapping("/my")
    public String myExchanges(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        List<RewardExchange> exchanges = rewardService.getUserExchanges(user.getId());
        model.addAttribute("exchanges", exchanges);
        model.addAttribute("user", user);
        return "volunteer/reward_my";
    }
    
    @PostMapping("/exchange/{rewardId}")
    @ResponseBody
    public Map<String, Object> exchangeReward(@PathVariable Long rewardId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }
            
            rewardService.exchangeReward(rewardId, user.getId());
            result.put("success", true);
            result.put("message", "兑换成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/cancel/{exchangeId}")
    @ResponseBody
    public Map<String, Object> cancelExchange(@PathVariable Long exchangeId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }
            
            rewardService.cancelExchange(exchangeId, user.getId());
            result.put("success", true);
            result.put("message", "取消兑换成功，积分已返还");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    // ==================== 管理员兑换管理功能 ====================
    
    @GetMapping("/admin/exchanges")
    public String adminExchangeList(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getUserType() != 3) {
            return "redirect:/login";
        }
        
        List<RewardExchange> exchanges = rewardService.getAllExchanges();
        model.addAttribute("exchanges", exchanges);
        return "admin/exchange_list";
    }
    
    @PostMapping("/admin/exchange/process/{exchangeId}")
    @ResponseBody
    public Map<String, Object> processExchange(@PathVariable Long exchangeId, 
                                                @RequestBody Map<String, Integer> params,
                                                HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null || user.getUserType() != 3) {
                result.put("success", false);
                result.put("message", "无权操作");
                return result;
            }
            
            Integer status = params.get("status");
            rewardService.processExchange(exchangeId, status);
            result.put("success", true);
            result.put("message", status == 1 ? "兑换已通过" : "兑换已拒绝");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
