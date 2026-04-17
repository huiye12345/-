package com.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日志工具类
 * 这是一个独立的工具类，不影响现有功能
 * 用于统一记录操作日志
 */
public class LogUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(LogUtil.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 记录操作日志
     * @param operation 操作描述
     * @param type 操作类型
     * @param username 操作用户
     * @param params 请求参数
     */
    public static void logOperation(String operation, String type, String username, Object params) {
        String time = LocalDateTime.now().format(formatter);
        String logMessage = String.format("[%s] [%s] 用户[%s] 执行[%s] 参数: %s", 
            time, type, username, operation, params);
        logger.info(logMessage);
    }
    
    /**
     * 记录登录日志
     * @param username 用户名
     * @param success 是否成功
     * @param message 消息
     */
    public static void logLogin(String username, boolean success, String message) {
        String time = LocalDateTime.now().format(formatter);
        String status = success ? "成功" : "失败";
        String logMessage = String.format("[%s] [LOGIN] 用户[%s] 登录%s: %s", 
            time, username, status, message);
        if (success) {
            logger.info(logMessage);
        } else {
            logger.warn(logMessage);
        }
    }
    
    /**
     * 记录业务操作日志
     * @param operation 操作描述
     * @param target 操作对象
     * @param details 详细信息
     */
    public static void logBusiness(String operation, String target, String details) {
        String time = LocalDateTime.now().format(formatter);
        String logMessage = String.format("[%s] [BUSINESS] [%s] 对象[%s] 详情: %s", 
            time, operation, target, details);
        logger.info(logMessage);
    }
    
    /**
     * 记录错误日志
     * @param operation 操作描述
     * @param error 错误信息
     */
    public static void logError(String operation, String error) {
        String time = LocalDateTime.now().format(formatter);
        String logMessage = String.format("[%s] [ERROR] [%s] 错误: %s", 
            time, operation, error);
        logger.error(logMessage);
    }
}
