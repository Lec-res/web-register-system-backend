-- 数据库初始化脚本
-- 注意：密码是 "123456" 经过BCrypt加密后的结果

-- 插入管理员用户
INSERT IGNORE INTO users (username, password, role, created_at, updated_at)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P3nKHUMhPYHijK', 'ADMIN', NOW(), NOW());

-- 插入普通用户
INSERT IGNORE INTO users (username, password, role, created_at, updated_at)
VALUES ('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P3nKHUMhPYHijK', 'USER', NOW(), NOW());

INSERT IGNORE INTO users (username, password, role, created_at, updated_at)
VALUES ('user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P3nKHUMhPYHijK', 'USER', NOW(), NOW());

-- 测试账户说明
-- admin / 123456 (管理员)
-- user1 / 123456 (普通用户)
-- user2 / 123456 (普通用户)