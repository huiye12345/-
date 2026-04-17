package com.community.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 这是一个独立的注解，不影响现有功能
 * 用于标记需要记录操作日志的方法
 * 
 * 使用示例：
 * @OperationLog(value = "创建用户", type = "CREATE")
 * public void createUser(User user) { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    
    /**
     * 操作描述
     */
    String value() default "";
    
    /**
     * 操作类型
     * CREATE - 创建
     * UPDATE - 更新
     * DELETE - 删除
     * QUERY - 查询
     * LOGIN - 登录
     * LOGOUT - 登出
     * OTHER - 其他
     */
    String type() default "OTHER";
    
    /**
     * 是否记录请求参数
     */
    boolean recordParams() default true;
    
    /**
     * 是否记录返回结果
     */
    boolean recordResult() default false;
}
