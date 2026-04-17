package com.community.config;

import com.community.util.PasswordEncoder;
import org.springframework.context.annotation.Configuration;

/**
 * 密码加密配置类
 * 这是一个独立的配置类，不影响现有功能
 * 如果需要启用密码加密，可以在此配置
 */
@Configuration
public class PasswordConfig {
    
    /**
     * 密码加密功能说明：
     * 
     * 1. 加密密码：
     *    String encodedPassword = PasswordEncoder.encode("原始密码");
     * 
     * 2. 验证密码：
     *    boolean matches = PasswordEncoder.matches("原始密码", "加密密码");
     * 
     * 3. 检查是否已加密：
     *    boolean isEncoded = PasswordEncoder.isEncoded("密码");
     * 
     * 注意：此功能是可选的，不影响现有系统的运行。
     * 如果要启用密码加密，需要修改UserService中的注册和登录逻辑。
     */
}
