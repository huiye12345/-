-- 创建数据库
CREATE DATABASE IF NOT EXISTS community_volunteer DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE community_volunteer;

-- 用户表
CREATE TABLE tb_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50),
    phone VARCHAR(20),
    address VARCHAR(200),
    user_type INT NOT NULL COMMENT '1-特殊人群 2-志愿者 3-管理员',
    status INT DEFAULT 0 COMMENT '0-待审核 1-正常 2-禁用',
    volunteer_hours INT DEFAULT 0,
    points INT DEFAULT 0,
    level INT DEFAULT 1,
    id_card VARCHAR(20),
    emergency_contact VARCHAR(50),
    emergency_phone VARCHAR(20),
    medical_info TEXT,
    avatar VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 服务需求表
CREATE TABLE tb_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    service_type VARCHAR(50),
    address VARCHAR(200),
    service_time DATETIME,
    status INT DEFAULT 0 COMMENT '0-待接单 1-已接单 2-服务中 3-已完成 4-已取消',
    volunteer_id BIGINT,
    accept_time DATETIME,
    complete_time DATETIME,
    duration INT COMMENT '服务时长(分钟)',
    is_emergency TINYINT DEFAULT 0,
    is_common TINYINT DEFAULT 0 COMMENT '常用需求',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES tb_user(id),
    FOREIGN KEY (volunteer_id) REFERENCES tb_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 评价表
CREATE TABLE tb_evaluation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id BIGINT NOT NULL,
    from_user_id BIGINT NOT NULL,
    to_user_id BIGINT NOT NULL,
    rating INT NOT NULL,
    content TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (request_id) REFERENCES tb_request(id),
    FOREIGN KEY (from_user_id) REFERENCES tb_user(id),
    FOREIGN KEY (to_user_id) REFERENCES tb_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 积分记录表
CREATE TABLE tb_points_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    points INT NOT NULL,
    type INT COMMENT '1-获得 2-消费',
    description VARCHAR(200),
    request_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES tb_user(id),
    FOREIGN KEY (request_id) REFERENCES tb_request(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- SOS紧急求助表
CREATE TABLE tb_sos_emergency (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    location VARCHAR(200),
    description TEXT,
    status INT DEFAULT 0 COMMENT '0-待响应 1-处理中 2-已解决',
    responder_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    respond_time DATETIME,
    resolve_time DATETIME,
    FOREIGN KEY (user_id) REFERENCES tb_user(id),
    FOREIGN KEY (responder_id) REFERENCES tb_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 奖励物品表
CREATE TABLE tb_reward (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    points INT NOT NULL,
    stock INT DEFAULT 0,
    type VARCHAR(50) COMMENT '优惠券/生活用品',
    status INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 奖励兑换表
CREATE TABLE tb_reward_exchange (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    reward_id BIGINT NOT NULL,
    points INT NOT NULL,
    status INT DEFAULT 0 COMMENT '0-待处理 1-已兑换 2-已取消',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES tb_user(id),
    FOREIGN KEY (reward_id) REFERENCES tb_reward(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 培训表
CREATE TABLE tb_training (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    trainer VARCHAR(50),
    training_time DATETIME,
    location VARCHAR(100),
    max_participants INT,
    current_participants INT DEFAULT 0,
    status INT DEFAULT 1 COMMENT '0-已取消 1-报名中 2-进行中 3-已结束',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 培训记录表
CREATE TABLE tb_training_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    training_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status INT DEFAULT 0 COMMENT '0-已报名 1-已完成 2-已取消',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (training_id) REFERENCES tb_training(id),
    FOREIGN KEY (user_id) REFERENCES tb_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 证书表
CREATE TABLE tb_certificate (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    certificate_no VARCHAR(50) UNIQUE,
    hours INT NOT NULL,
    content TEXT,
    issue_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES tb_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 常用需求模板表
CREATE TABLE tb_common_request_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    service_type VARCHAR(50),
    content TEXT,
    address VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES tb_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入默认管理员账号
INSERT INTO tb_user (username, password, real_name, phone, user_type, status) 
VALUES ('admin', '123456', '系统管理员', '13800138000', 3, 1);
