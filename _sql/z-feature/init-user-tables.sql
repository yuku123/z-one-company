-- TASK001: 用户相关基础表
-- 数据库: biz_service

USE biz_service;

-- 1. 用户表 (sys_user)
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    user_name VARCHAR(64) NOT NULL COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码',
    real_name VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
    email VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    phone VARCHAR(32) DEFAULT NULL COMMENT '手机号',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    tenant_id VARCHAR(64) DEFAULT NULL COMMENT '租户ID',
    dept_id BIGINT DEFAULT NULL COMMENT '部门ID',
    last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_name (user_name),
    KEY idx_tenant_id (tenant_id),
    KEY idx_status (status),
    KEY idx_phone (phone),
    KEY idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 角色表 (sys_role)
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    role_code VARCHAR(64) NOT NULL COMMENT '角色编码',
    role_name VARCHAR(64) NOT NULL COMMENT '角色名称',
    description VARCHAR(256) DEFAULT NULL COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    tenant_id VARCHAR(64) DEFAULT NULL COMMENT '租户ID',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code),
    KEY idx_tenant_id (tenant_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 3. 用户角色关联表 (sys_user_role)
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id),
    KEY idx_user_id (user_id),
    KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 4. 登录日志表 (sys_login_log)
CREATE TABLE IF NOT EXISTS sys_login_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT DEFAULT NULL COMMENT '用户ID',
    user_name VARCHAR(64) DEFAULT NULL COMMENT '用户名',
    tenant_id VARCHAR(64) DEFAULT NULL COMMENT '租户ID',
    login_type VARCHAR(32) DEFAULT 'PASSWORD' COMMENT '登录类型：PASSWORD/SMS/QR_CODE',
    ip_address VARCHAR(64) DEFAULT NULL COMMENT 'IP地址',
    user_agent VARCHAR(512) DEFAULT NULL COMMENT '用户代理',
    browser VARCHAR(128) DEFAULT NULL COMMENT '浏览器',
    os VARCHAR(128) DEFAULT NULL COMMENT '操作系统',
    status TINYINT DEFAULT 1 COMMENT '状态：0-失败，1-成功',
    error_msg VARCHAR(512) DEFAULT NULL COMMENT '错误信息',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_tenant_id (tenant_id),
    KEY idx_gmt_create (gmt_create)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- 初始化测试用户 (用户名: testuser, 密码: Test@123456 BCrypt加密)
INSERT INTO sys_user (user_name, password, real_name, email, phone, status) VALUES
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '测试用户', 'test@example.com', '13800138000', 1)
ON DUPLICATE KEY UPDATE user_name=user_name;

-- 初始化角色
INSERT INTO sys_role (role_code, role_name, description, status) VALUES
('USER', '普通用户', '普通用户角色', 1)
ON DUPLICATE KEY UPDATE role_code=role_code;