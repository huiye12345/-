# 社区特殊人群志愿服务管理系统

[![Java](https://img.shields.io/badge/Java-1.8-blue.svg)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.14-green.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> 基于Spring Boot的社区特殊人群志愿服务Web端管理系统，为社区特殊人群与志愿者搭建互助桥梁。

---

## 📋 项目简介

本系统是一个面向社区特殊人群（老年人、残障人士等）的志愿服务管理平台，通过信息化手段连接特殊人群与志愿者，提供便捷的服务需求发布、志愿者接单、公益活动组织、积分激励等功能。

### 核心功能

- 👥 **三类角色管理**：特殊人群、志愿者、管理员
- 📝 **服务需求管理**：发布需求、接单服务、完成评价
- 🎉 **公益活动管理**：活动发布、报名参与、签到完成
- 🎁 **积分商城系统**：服务积分、商品兑换
- 🆘 **SOS紧急求助**：一键求助、快速响应
- 🤖 **AI智能助手**：智能问答、服务指导

---

## 🏗️ 技术架构

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.14 | 核心框架 |
| MyBatis-Plus | 3.5.3.1 | ORM框架 |
| MySQL | 8.0 | 数据库 |
| Thymeleaf | 3.0 | 模板引擎 |
| Spring Security Crypto | 5.x | 密码加密 |

### 前端技术栈

| 技术 | 说明 |
|------|------|
| HTML5 | 页面结构 |
| CSS3 | 样式设计 |
| JavaScript | 交互逻辑 |
| ECharts | 数据可视化 |

---

## 🚀 快速开始

### 环境要求

- JDK 1.8+
- MySQL 8.0+
- Maven 3.6+

### 安装步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/huiye12345/community-volunteer-service.git
   cd community-volunteer-service
   ```

2. **创建数据库**
   ```sql
   -- 创建数据库
   CREATE DATABASE community_volunteer CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   
   -- 执行初始化SQL（位于 src/main/resources/sql/database.sql）
   ```

3. **配置数据库**
   
   修改 `src/main/resources/application.yml`：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/community_volunteer?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
       username: root
       password: your_password
   ```

4. **运行项目**
   ```bash
   mvn spring-boot:run
   ```

5. **访问系统**
   
   打开浏览器访问：http://localhost:8080

### 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin |
| 特殊人群 | special1 | 123456 |
| 志愿者 | volun1 | 123456 |

---

## 📁 项目结构

```
community-volunteer-service/
├── src/main/java/com/community/
│   ├── annotation/          # 自定义注解
│   ├── config/              # 配置类
│   ├── controller/          # 控制器层
│   ├── entity/              # 实体类
│   ├── enums/               # 枚举类
│   ├── interceptor/         # 拦截器
│   ├── mapper/              # 数据访问层
│   ├── service/             # 业务逻辑层
│   └── util/                # 工具类
├── src/main/resources/
│   ├── static/              # 静态资源
│   ├── templates/           # 页面模板
│   ├── mapper/              # MyBatis映射文件
│   └── sql/                 # 数据库脚本
└── pom.xml                  # Maven配置
```

---

## ✨ 特色功能

### 🔐 密码加密

采用BCrypt算法对用户密码进行加密存储，保障用户信息安全。

```java
// 密码加密示例
String encodedPassword = PasswordEncoder.encode("原始密码");
boolean matches = PasswordEncoder.matches("原始密码", encodedPassword);
```

### 📝 操作日志

使用注解方式记录关键操作日志，便于系统审计和问题追踪。

```java
@OperationLog(value = "审核通过用户", type = "UPDATE")
public Map<String, Object> approveUser(Long userId) {
    // 业务逻辑
}
```

### 📄 分页查询

封装统一的分页查询工具，支持MyBatis-Plus分页。

```java
PageQuery query = new PageQuery();
query.setPage(1);
query.setSize(10);
PageResult<User> result = new PageResult<>(page, size, total, records);
```

### 🤖 AI智能助手

集成AI接口，为用户提供智能问答服务。

---

## 🌿 分支说明

| 分支 | 说明 | 用途 |
|------|------|------|
| `backup-original` | 原始版本 | 基础功能完整，适合作为毕设提交 |
| `enhancement-safe` | 增强版本 | 包含密码加密、操作日志、分页等扩展功能 |

### 切换分支

```bash
# 切换到原始版本
git checkout backup-original

# 切换到增强版本
git checkout enhancement-safe
```

---

## 📊 数据库设计

### 核心表结构

- `tb_user` - 用户表
- `tb_request` - 服务需求表
- `tb_activity` - 活动表
- `tb_activity_record` - 活动参与记录表
- `tb_reward` - 积分商品表
- `tb_reward_exchange` - 积分兑换记录表
- `tb_points_record` - 积分记录表
- `tb_sos_emergency` - SOS紧急求助表


---

## 🎯 适用场景

- 🎓 计算机专业毕业设计
- 🏘️ 社区志愿服务管理
- 📚 Java Web学习参考
- 🔧 Spring Boot项目模板

---

## 📸 界面预览

### 登录页面
用户登录入口，支持三种角色。

### 管理员后台
- 数据看板（ECharts可视化）
- 用户管理
- 服务需求管理
- 活动管理
- 积分商城管理

### 特殊人群端
- 发布服务需求
- 查看需求状态
- SOS紧急求助
- 个人信息管理

### 志愿者端
- 需求大厅（接单）
- 我的服务
- 公益活动
- 积分商城
- AI助手

---


## 👨‍💻 作者

- **huiye12345** - [GitHub](https://github.com/huiye12345)

---

> 💡 **提示**：本项目为毕业设计作品，代码仅供参考学习，请勿直接用于生产环境。
