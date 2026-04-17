package com.community.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.community.entity.User;
import com.community.enums.UserType;
import com.community.service.UserService;

@Controller
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Value("${file.upload.path:uploads/}")
    private String uploadPath;
    
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    @PostMapping("/login")
    @ResponseBody
    public Map<String, Object> login(@RequestParam String username, 
                                      @RequestParam String password,
                                      HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userService.login(username, password);
            session.setAttribute("user", user);
            result.put("success", true);
            result.put("userType", user.getUserType());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    
    @PostMapping("/register")
    @ResponseBody
    public Map<String, Object> register(User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            userService.register(user);
            result.put("success", true);
            result.put("message", "注册成功，请等待审核");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    
    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        
        Integer userType = user.getUserType();
        if (userType != null && userType.equals(UserType.SPECIAL.getCode())) {
            return "special/home";
        } else if (userType != null && userType.equals(UserType.VOLUNTEER.getCode())) {
            return "volunteer/home";
        } else if (userType != null && userType.equals(UserType.ADMIN.getCode())) {
            return "admin/home";
        } else {
            return "redirect:/login";
        }
    }
    
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        user = userService.getUserById(user.getId());
        session.setAttribute("user", user);
        model.addAttribute("user", user);
        
        // 添加头像文字
        String avatarText = "";
        if (user.getRealName() != null && !user.getRealName().isEmpty()) {
            avatarText = user.getRealName().substring(0, 1);
        } else if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            avatarText = user.getUsername().substring(0, 1);
        }
        model.addAttribute("avatarText", avatarText);
        
        // 添加显示名称
        String displayName = user.getRealName() != null && !user.getRealName().isEmpty() 
            ? user.getRealName() : user.getUsername();
        model.addAttribute("displayName", displayName);
        
        // 添加角色文字
        String roleText = "";
        if (user.getUserType() != null) {
            switch (user.getUserType()) {
                case 1: roleText = "管理员"; break;
                case 2: roleText = "志愿者"; break;
                case 3: roleText = "特殊人群"; break;
            }
        }
        model.addAttribute("roleText", roleText);
        
        // 志愿者等级信息
        if (user.getUserType() != null && user.getUserType() == 2 && user.getLevel() != null) {
            model.addAttribute("showLevelInfo", true);
            String levelName = getLevelName(user.getLevel());
            model.addAttribute("levelTitle", "LV" + user.getLevel() + " - " + levelName);
            
            // 计算下一级所需时长
            int nextLevelHours = user.getLevel() * 10;
            int currentHours = user.getVolunteerHours() != null ? user.getVolunteerHours() : 0;
            int needHours = nextLevelHours - currentHours;
            if (needHours < 0) needHours = 0;
            model.addAttribute("nextLevelHours", needHours);
            
            // 计算进度百分比
            int progress = (int) ((currentHours * 100.0) / nextLevelHours);
            if (progress > 100) progress = 100;
            model.addAttribute("progressPercent", progress);
        } else {
            model.addAttribute("showLevelInfo", false);
        }
        
        return "profile";
    }
    
    private String getLevelName(int level) {
        if (level >= 10) return "荣誉志愿者";
        if (level >= 7) return "高级志愿者";
        if (level >= 4) return "中级志愿者";
        return "初级志愿者";
    }
    
    @PostMapping("/profile/update")
    @ResponseBody
    public Map<String, Object> updateProfile(User user, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User currentUser = (User) session.getAttribute("user");
            user.setId(currentUser.getId());
            
            // 如果密码为空，保留原密码
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                user.setPassword(currentUser.getPassword());
            }
            
            // 检查用户名是否被其他用户使用
            if (!user.getUsername().equals(currentUser.getUsername())) {
                User existUser = userService.getUserByUsername(user.getUsername());
                if (existUser != null && !existUser.getId().equals(currentUser.getId())) {
                    throw new RuntimeException("用户名已被其他用户使用");
                }
            }
            
            userService.updateUser(user);
            session.setAttribute("user", userService.getUserById(user.getId()));
            result.put("success", true);
            result.put("message", "更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/profile/avatar")
    @ResponseBody
    public Map<String, Object> uploadAvatar(@RequestParam("avatar") MultipartFile file, 
                                             HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }
            
            if (file.isEmpty()) {
                result.put("success", false);
                result.put("message", "请选择图片文件");
                return result;
            }
            
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                result.put("success", false);
                result.put("message", "只能上传图片文件");
                return result;
            }
            
            // 检查文件大小（2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                result.put("success", false);
                result.put("message", "图片大小不能超过2MB");
                return result;
            }
            
            // 创建上传目录（使用新的头像文件夹）
            String baseDir = System.getProperty("user.dir");
            String avatarDir = baseDir + File.separator + "头像" + File.separator;
            File dir = new File(avatarDir);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    result.put("success", false);
                    result.put("message", "创建上传目录失败");
                    return result;
                }
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = ".jpg";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;
            File destFile = new File(avatarDir + filename);
            
            // 保存文件
            file.transferTo(destFile);
            
            // 删除旧头像文件
            User user = userService.getUserById(currentUser.getId());
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                String oldFilename = user.getAvatar().substring(user.getAvatar().lastIndexOf("/") + 1);
                File oldFile = new File(avatarDir + oldFilename);
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }
            
            // 更新用户头像路径
            String avatarUrl = "/avatar/" + filename;
            user.setAvatar(avatarUrl);
            userService.updateUser(user);
            
            // 更新session中的用户信息
            session.setAttribute("user", user);
            
            result.put("success", true);
            result.put("message", "头像上传成功");
            result.put("avatarUrl", avatarUrl);
        } catch (IOException e) {
            result.put("success", false);
            result.put("message", "文件上传失败：" + e.getMessage());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "上传失败：" + e.getMessage());
        }
        return result;
    }
}
