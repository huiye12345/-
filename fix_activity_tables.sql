USE community_volunteer;

DROP TABLE IF EXISTS tb_activity_record;
DROP TABLE IF EXISTS tb_activity;

CREATE TABLE tb_activity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    location VARCHAR(200),
    activity_time DATETIME,
    max_participants INT DEFAULT 0,
    current_participants INT DEFAULT 0,
    points INT DEFAULT 0,
    hours INT DEFAULT 0,
    status INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE tb_activity_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status INT DEFAULT 0,
    sign_in_time DATETIME,
    complete_time DATETIME,
    points_earned INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (activity_id) REFERENCES tb_activity(id),
    FOREIGN KEY (user_id) REFERENCES tb_user(id),
    UNIQUE KEY uk_activity_user (activity_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
