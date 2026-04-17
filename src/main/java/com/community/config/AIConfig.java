package com.community.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * AI 服务配置类
 * 配置硅基流动 API 相关参数
 */
@Configuration
@ConfigurationProperties(prefix = "ai")
public class AIConfig {
    
    /**
     * 是否启用 AI 功能
     */
    private boolean enabled = true;
    
    /**
     * API 密钥
     */
    private String apiKey = "";
    
    /**
     * API 基础地址
     */
    private String baseUrl = "https://api.siliconflow.cn/v1";
    
    /**
     * 默认使用的模型
     */
    private String defaultModel = "Pro/moonshotai/Kimi-K2-5";
    
    /**
     * 最大 token 数
     */
    private int maxTokens = 2048;
    
    /**
     * 温度参数（创造性程度）
     */
    private double temperature = 0.7;
    
    /**
     * 超时时间（秒）
     */
    private int timeout = 30;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getDefaultModel() {
        return defaultModel;
    }

    public void setDefaultModel(String defaultModel) {
        this.defaultModel = defaultModel;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
