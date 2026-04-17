-- 为已有数据库添加头像字段
USE community_volunteer;

-- 添加 avatar 字段到用户表
ALTER TABLE tb_user ADD COLUMN avatar VARCHAR(500) AFTER medical_info;
