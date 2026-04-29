-- z-mist 密钥管理平台数据库
CREATE DATABASE IF NOT EXISTS z_mist CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE z_mist;

-- 密钥主表
DROP TABLE IF EXISTS `z_mist_secret_info`;
CREATE TABLE IF NOT EXISTS `z_mist_secret_info` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `secret_key` varchar(255) NOT NULL COMMENT '密钥标识（唯一）',
    `secret_name` varchar(128) NOT NULL COMMENT '密钥名称',
    `group` varchar(128) DEFAULT NULL COMMENT '密钥分组',
    `app_name` varchar(128) DEFAULT NULL COMMENT '应用名',
    `namespace` varchar(128) DEFAULT '' COMMENT '命名空间',
    `encrypted_value` longtext NOT NULL COMMENT '加密后的密钥值',
    `value_md5` varchar(32) DEFAULT NULL COMMENT '密钥值MD5',
    `encrypt_algorithm` varchar(32) DEFAULT 'AES' COMMENT '加密算法',
    `key_version` varchar(32) DEFAULT 'v1' COMMENT '密钥版本',
    `secret_type` varchar(32) DEFAULT 'text' COMMENT '密钥类型（text/cert/password/key）',
    `description` varchar(512) DEFAULT NULL COMMENT '密钥描述',
    `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime NOT NULL COMMENT '修改时间',
    `creator_staff_no` varchar(255) COMMENT '创建人工号',
    `creator_staff_nick_nm` varchar(255) COMMENT '创建人昵称',
    `source_ip` varchar(50) DEFAULT NULL COMMENT '创建IP',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_secret_key_group_namespace` (`secret_key`,`group`,`namespace`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='密钥信息表';

-- 密钥历史版本表
DROP TABLE IF EXISTS `z_mist_secret_history`;
CREATE TABLE IF NOT EXISTS `z_mist_secret_history` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `nid` bigint(20) NOT NULL COMMENT '密钥历史ID',
    `secret_key` varchar(255) NOT NULL COMMENT '密钥标识',
    `group` varchar(128) DEFAULT NULL,
    `app_name` varchar(128) DEFAULT NULL,
    `encrypted_value` longtext NOT NULL COMMENT '加密后的密钥值',
    `value_md5` varchar(32) DEFAULT NULL,
    `key_version` varchar(32) DEFAULT NULL COMMENT '密钥版本',
    `op_type` char(10) DEFAULT NULL COMMENT '操作类型（新增/修改/删除）',
    `operator_staff_no` varchar(255) COMMENT '操作人工号',
    `operator_ip` varchar(50) DEFAULT NULL COMMENT '操作IP',
    `gmt_create` datetime NOT NULL COMMENT '创建时间',
    `gmt_modified` datetime NOT NULL COMMENT '修改时间',
    `namespace` varchar(128) DEFAULT '' COMMENT '命名空间',
    PRIMARY KEY (`id`),
    KEY `idx_gmt_modified` (`gmt_modified`),
    KEY `idx_secret_key` (`secret_key`,`group`,`namespace`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='密钥历史版本表';

-- 密钥访问授权表
DROP TABLE IF EXISTS `z_mist_secret_acl`;
CREATE TABLE IF NOT EXISTS `z_mist_secret_acl` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `secret_key` varchar(255) NOT NULL COMMENT '密钥标识',
    `group` varchar(128) DEFAULT NULL,
    `namespace` varchar(128) DEFAULT '',
    `authorized_app` varchar(128) NOT NULL COMMENT '授权应用',
    `authorized_env` varchar(32) DEFAULT NULL COMMENT '授权环境（dev/test/prod）',
    `permission_level` varchar(16) DEFAULT 'read' COMMENT '权限级别（read/decrypt）',
    `expire_time` datetime DEFAULT NULL COMMENT '授权过期时间',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_secret_app_env` (`secret_key`,`group`,`namespace`,`authorized_app`,`authorized_env`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='密钥访问授权表';

-- 应用注册表
DROP TABLE IF EXISTS `z_mist_app_info`;
CREATE TABLE IF NOT EXISTS `z_mist_app_info` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `app_name` varchar(128) NOT NULL COMMENT '应用名',
    `app_secret` varchar(255) NOT NULL COMMENT '应用密钥',
    `app_type` varchar(32) DEFAULT 'server' COMMENT '应用类型（server/client）',
    `namespace` varchar(128) DEFAULT '' COMMENT '所属命名空间',
    `description` varchar(512) DEFAULT NULL,
    `enabled` tinyint(1) DEFAULT 1 COMMENT '是否启用',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_app_name_namespace` (`app_name`,`namespace`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用注册表';

-- 用户表
DROP TABLE IF EXISTS `z_mist_users`;
CREATE TABLE IF NOT EXISTS `z_mist_users` (
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(500) NOT NULL COMMENT '密码（BCrypt加密）',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
DROP TABLE IF EXISTS `z_mist_roles`;
CREATE TABLE IF NOT EXISTS `z_mist_roles` (
    `username` varchar(50) NOT NULL COMMENT '关联用户名',
    `role` varchar(50) NOT NULL COMMENT '角色名',
    PRIMARY KEY (`username`,`role`),
    KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色映射表';

-- 权限表
DROP TABLE IF EXISTS `z_mist_permissions`;
CREATE TABLE IF NOT EXISTS `z_mist_permissions` (
    `role` varchar(50) NOT NULL COMMENT '角色名',
    `resource` varchar(255) NOT NULL COMMENT '资源标识',
    `action` varchar(8) NOT NULL COMMENT '权限操作（read/write/delete）',
    PRIMARY KEY (`role`,`resource`,`action`),
    KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限映射表';

-- 初始化默认用户（用户名：admin，密码：admin，BCrypt加密后的值）
INSERT IGNORE INTO `z_mist_users` (`username`, `password`, `enabled`)
VALUES ('admin', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', 1);

-- 初始化默认角色（admin用户关联管理员角色）
INSERT IGNORE INTO `z_mist_roles` (`username`, `role`)
VALUES ('admin', 'ROLE_ADMIN');