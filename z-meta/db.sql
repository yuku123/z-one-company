-- z-meta 元数据平台数据库脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS z_meta CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE z_meta;

-- 1. 租户域表
DROP TABLE IF EXISTS `z_tenant`;
CREATE TABLE IF NOT EXISTS `z_tenant` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `tenant_code` varchar(64) NOT NULL COMMENT '租户编码',
    `tenant_name` varchar(128) NOT NULL COMMENT '租户名称',
    `tenant_type` varchar(32) DEFAULT 'enterprise' COMMENT '租户类型（system/enterprise/personal）',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态（1启用/0禁用）',
    `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
    `logo_url` varchar(500) DEFAULT NULL COMMENT 'Logo',
    `description` varchar(500) DEFAULT NULL COMMENT '描述',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_code` (`tenant_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户域表';

-- 2. 应用表
DROP TABLE IF EXISTS `z_application`;
CREATE TABLE IF NOT EXISTS `z_application` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
    `app_code` varchar(64) NOT NULL COMMENT '应用编码',
    `app_name` varchar(128) NOT NULL COMMENT '应用名称',
    `app_type` varchar(32) DEFAULT 'web' COMMENT '应用类型（web/api/mobile/miniapp）',
    `description` varchar(500) DEFAULT NULL COMMENT '描述',
    `base_url` varchar(255) DEFAULT NULL COMMENT '基础URL',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态',
    `owner` varchar(64) DEFAULT NULL COMMENT '负责人',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_app_code_tenant` (`app_code`,`tenant_id`),
    KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用表';

-- 3. 字典类型表
DROP TABLE IF EXISTS `z_dict_type`;
CREATE TABLE IF NOT EXISTS `z_dict_type` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
    `dict_code` varchar(64) NOT NULL COMMENT '字典编码',
    `dict_name` varchar(128) NOT NULL COMMENT '字典名称',
    `dict_type` varchar(32) DEFAULT 'custom' COMMENT '字典类型（system/custom）',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态',
    `description` varchar(500) DEFAULT NULL COMMENT '描述',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_code_tenant` (`dict_code`,`tenant_id`),
    KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- 4. 字典项表
DROP TABLE IF EXISTS `z_dict_item`;
CREATE TABLE IF NOT EXISTS `z_dict_item` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `dict_id` bigint(20) NOT NULL COMMENT '字典类型ID',
    `item_code` varchar(64) NOT NULL COMMENT '字典项编码',
    `item_name` varchar(128) NOT NULL COMMENT '字典项名称',
    `item_value` varchar(255) NOT NULL COMMENT '字典项值',
    `sort_order` int(11) DEFAULT 0 COMMENT '排序',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态',
    `parent_id` bigint(20) DEFAULT NULL COMMENT '父级ID',
    `ext_props` text COMMENT '扩展属性',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_dict_id` (`dict_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典项表';

-- 5. 接口清单表
DROP TABLE IF EXISTS `z_api`;
CREATE TABLE IF NOT EXISTS `z_api` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `app_id` bigint(20) NOT NULL COMMENT '应用ID',
    `api_path` varchar(255) NOT NULL COMMENT '接口路径',
    `api_method` varchar(16) DEFAULT 'GET' COMMENT '请求方法',
    `api_name` varchar(128) NOT NULL COMMENT '接口名称',
    `api_version` varchar(32) DEFAULT 'v1' COMMENT '接口版本',
    `description` varchar(500) DEFAULT NULL COMMENT '描述',
    `request_params` text COMMENT '请求参数JSON',
    `response_params` text COMMENT '响应参数JSON',
    `auth_type` varchar(32) DEFAULT 'bearer' COMMENT '认证类型',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态',
    `deprecated` tinyint(1) DEFAULT 0 COMMENT '是否废弃',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_app_id` (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='接口清单表';

-- 6. 用户表（参考 z-config）
DROP TABLE IF EXISTS `z_users`;
CREATE TABLE IF NOT EXISTS `z_users` (
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(500) NOT NULL COMMENT '密码（BCrypt加密）',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 7. 角色表
DROP TABLE IF EXISTS `z_roles`;
CREATE TABLE IF NOT EXISTS `z_roles` (
    `username` varchar(50) NOT NULL COMMENT '关联z_users.username',
    `role` varchar(50) NOT NULL COMMENT '角色名',
    PRIMARY KEY (`username`,`role`),
    KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色映射表';

-- 初始化默认用户（用户名：admin，密码：admin，BCrypt加密）
INSERT IGNORE INTO `z_users` (`username`, `password`, `enabled`)
VALUES ('admin', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', 1);

-- 初始化默认角色
INSERT IGNORE INTO `z_roles` (`username`, `role`)
VALUES ('admin', 'ROLE_ADMIN');

-- 初始化默认租户
INSERT IGNORE INTO `z_tenant` (`id`, `tenant_code`, `tenant_name`, `tenant_type`, `status`, `description`)
VALUES (1, 'default', '默认租户', 'system', 1, '系统默认租户');