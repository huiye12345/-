USE community_volunteer;

DROP TABLE IF EXISTS tb_reward_exchange;
DROP TABLE IF EXISTS tb_reward;

CREATE TABLE tb_reward (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    points INT NOT NULL DEFAULT 0,
    stock INT DEFAULT 0,
    type VARCHAR(50),
    status INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE tb_reward_exchange (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    reward_id BIGINT NOT NULL,
    points INT NOT NULL DEFAULT 0,
    status INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES tb_user(id),
    FOREIGN KEY (reward_id) REFERENCES tb_reward(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO tb_reward (name, description, points, stock, type, status) VALUES
('Notebook', 'Custom notebook for volunteers', 100, 50, 'Physical', 1),
('T-Shirt', 'Volunteer T-Shirt', 200, 30, 'Physical', 1),
('Thermos', 'High quality thermos', 300, 20, 'Physical', 1),
('Umbrella', 'Beautiful umbrella', 150, 40, 'Physical', 1),
('Phone Credit 10', '10 yuan phone credit', 500, 100, 'Virtual', 1),
('Supermarket Coupon 20', '20 yuan supermarket coupon', 800, 50, 'Coupon', 1);
