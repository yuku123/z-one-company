-- ============================================
-- CTC 模块表结构 - 使用 z_ctc_ 前缀
-- ============================================

-- ============================================
-- 1. 用户表
-- ============================================
DROP TABLE IF EXISTS z_ctc_user;
CREATE TABLE z_ctc_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    user_name VARCHAR(64) NOT NULL COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码',
    real_name VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
    email VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    phone VARCHAR(32) DEFAULT NULL COMMENT '手机号',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    tenant_code VARCHAR(64) DEFAULT NULL COMMENT '租户编码',
    dept_id BIGINT DEFAULT NULL COMMENT '部门ID',
    last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_name (user_name),
    KEY idx_tenant_code (tenant_code),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================
-- 2. 角色表
-- ============================================
DROP TABLE IF EXISTS z_ctc_role;
CREATE TABLE z_ctc_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    role_code VARCHAR(64) NOT NULL COMMENT '角色编码',
    role_name VARCHAR(64) NOT NULL COMMENT '角色名称',
    description VARCHAR(256) DEFAULT NULL COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    tenant_code VARCHAR(64) DEFAULT NULL COMMENT '租户编码',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code),
    KEY idx_tenant_code (tenant_code),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ============================================
-- 3. 权限表
-- ============================================
DROP TABLE IF EXISTS z_ctc_permission;
CREATE TABLE z_ctc_permission (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    perm_code VARCHAR(128) NOT NULL COMMENT '权限编码',
    perm_name VARCHAR(64) NOT NULL COMMENT '权限名称',
    perm_type VARCHAR(32) NOT NULL COMMENT '权限类型：MENU-菜单，BUTTON-按钮，API-接口',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    path VARCHAR(256) DEFAULT NULL COMMENT '权限路径',
    icon VARCHAR(64) DEFAULT NULL COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    tenant_code VARCHAR(64) DEFAULT NULL COMMENT '租户编码',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_perm_code (perm_code),
    KEY idx_parent_id (parent_id),
    KEY idx_tenant_code (tenant_code),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- ============================================
-- 4. 用户角色关联表
-- ============================================
DROP TABLE IF EXISTS z_ctc_user_role;
CREATE TABLE z_ctc_user_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id),
    KEY idx_user_id (user_id),
    KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ============================================
-- 5. 角色权限关联表
-- ============================================
DROP TABLE IF EXISTS z_ctc_role_permission;
CREATE TABLE z_ctc_role_permission (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    KEY idx_role_id (role_id),
    KEY idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- ============================================
-- 6. 审计日志表
-- ============================================
DROP TABLE IF EXISTS z_ctc_audit_log;
CREATE TABLE z_ctc_audit_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    operation_type VARCHAR(64) NOT NULL COMMENT '操作类型：LOGIN/LOGOUT/CREATE/UPDATE/DELETE',
    operation_desc VARCHAR(256) DEFAULT NULL COMMENT '操作描述',
    user_id BIGINT DEFAULT NULL COMMENT '用户ID',
    user_name VARCHAR(64) DEFAULT NULL COMMENT '用户名',
    tenant_code VARCHAR(64) DEFAULT NULL COMMENT '租户编码',
    ip_address VARCHAR(64) DEFAULT NULL COMMENT 'IP地址',
    user_agent VARCHAR(512) DEFAULT NULL COMMENT '用户代理',
    request_url VARCHAR(512) DEFAULT NULL COMMENT '请求URL',
    request_method VARCHAR(32) DEFAULT NULL COMMENT '请求方法',
    request_params TEXT DEFAULT NULL COMMENT '请求参数',
    response_data TEXT DEFAULT NULL COMMENT '响应数据',
    execution_time INT DEFAULT 0 COMMENT '执行时间（毫秒）',
    status TINYINT DEFAULT 1 COMMENT '状态：0-失败，1-成功',
    error_msg TEXT DEFAULT NULL COMMENT '错误信息',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_tenant_code (tenant_code),
    KEY idx_operation_type (operation_type),
    KEY idx_gmt_create (gmt_create)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志表';

-- ============================================
-- 7. 登录日志表
-- ============================================
DROP TABLE IF EXISTS z_ctc_login_log;
CREATE TABLE z_ctc_login_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT DEFAULT NULL COMMENT '用户ID',
    user_name VARCHAR(64) DEFAULT NULL COMMENT '用户名',
    tenant_code VARCHAR(64) DEFAULT NULL COMMENT '租户编码',
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
    KEY idx_tenant_code (tenant_code),
    KEY idx_gmt_create (gmt_create)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- ============================================
-- 8. 验证码表
-- ============================================
DROP TABLE IF EXISTS z_ctc_verify_code;
CREATE TABLE z_ctc_verify_code (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    biz_type VARCHAR(32) NOT NULL COMMENT '业务类型: REGISTER-注册, LOGIN-登录, RESET_PWD-重置密码',
    code_type VARCHAR(32) NOT NULL COMMENT '验证码类型: PHONE-手机, EMAIL-邮箱',
    receiver VARCHAR(128) NOT NULL COMMENT '接收者(手机号/邮箱)',
    code VARCHAR(16) NOT NULL COMMENT '验证码',
    expire_time DATETIME NOT NULL COMMENT '过期时间',
    used TINYINT NOT NULL DEFAULT 0 COMMENT '是否已使用: 0-否, 1-是',
    gmt_create DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_receiver_expire (receiver, expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验证码记录表';

-- ============================================
-- 9. 租户表
-- ============================================
DROP TABLE IF EXISTS z_ctc_tenant;
CREATE TABLE z_ctc_tenant (
    tenant_code VARCHAR(64) NOT NULL COMMENT '租户编码（主键）',
    tenant_name VARCHAR(100) NOT NULL COMMENT '租户名称',
    contact_name VARCHAR(50) DEFAULT NULL COMMENT '联系人',
    contact_phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    contact_email VARCHAR(100) DEFAULT NULL COMMENT '联系邮箱',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1-启用 0-禁用',
    expire_time DATETIME DEFAULT NULL COMMENT '过期时间',
    gmt_create DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
    PRIMARY KEY (tenant_code),
    UNIQUE KEY uk_tenant_name (tenant_name),
    KEY idx_status (status),
    KEY idx_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入默认租户
INSERT INTO z_ctc_tenant (tenant_code, tenant_name, contact_name, contact_phone, contact_email, status, expire_time)
VALUES ('default', '默认租户', '系统管理员', '13800138000', 'admin@example.com', 1, '2099-12-31 23:59:59');

-- 初始化超级管理员角色
INSERT INTO z_ctc_role (role_code, role_name, description, status, tenant_code) VALUES
    ('SUPER_ADMIN', '超级管理员', '系统超级管理员，拥有所有权限', 1, 'default'),
    ('ADMIN', '管理员', '系统管理员', 1, 'default'),
    ('USER', '普通用户', '普通用户角色', 1, 'default');

-- 初始化常用权限
INSERT INTO z_ctc_permission (perm_code, perm_name, perm_type, parent_id, path, sort_order, status, tenant_code) VALUES
    ('system', '系统管理', 'MENU', 0, '/system', 1, 1, 'default'),
    ('system:user', '用户管理', 'MENU', 1, '/system/user', 1, 1, 'default'),
    ('system:role', '角色管理', 'MENU', 1, '/system/role', 2, 1, 'default'),
    ('system:permission', '权限管理', 'MENU', 1, '/system/permission', 3, 1, 'default'),
    ('system:audit', '审计日志', 'MENU', 1, '/system/audit', 4, 1, 'default');

-- 初始化超级管理员账号（密码：admin123，BCrypt加密）
INSERT INTO z_ctc_user (user_name, password, real_name, email, phone, status, tenant_code, last_login_time)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '超级管理员', 'admin@example.com', '13800138000', 1, 'default', NOW());

-- 为超级管理员分配角色
INSERT INTO z_ctc_user_role (user_id, role_id) VALUES (1, 1);
