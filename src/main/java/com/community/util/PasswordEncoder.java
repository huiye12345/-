package com.community.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密工具类
 * 用于用户密码的加密和验证
 * 这是一个独立的工具类，不影响现有功能
 */
public class PasswordEncoder {
    
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    /**
     * 加密密码
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public static String encode(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            return null;
        }
        return encoder.encode(rawPassword);
    }
    
    /**
     * 验证密码
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return encoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * 检查密码是否已经是加密格式
     * BCrypt加密后的密码以$2a$、$2b$或$2y$开头
     * @param password 密码
     * @return 是否已加密
     */
    public static boolean isEncoded(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return password.startsWith("$2a$") || 
               password.startsWith("$2b$") || 
               password.startsWith("$2y$");
    }
}
