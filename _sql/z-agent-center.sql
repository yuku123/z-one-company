-- =============================================
-- z-agent-center 表结构
-- 表前缀: z_agent_
-- =============================================

-- 1. Agent应用配置表
CREATE TABLE IF NOT EXISTS `z_agent_app` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `app_code` VARCHAR(64) NOT NULL COMMENT '应用编码(唯一标识)',
    `app_name` VARCHAR(128) NOT NULL COMMENT '应用名称',
    `description` VARCHAR(512) COMMENT '应用描述',
    `icon_url` VARCHAR(256) COMMENT '图标URL',
    `prompt` TEXT COMMENT '系统提示词',
    `model_name` VARCHAR(128) DEFAULT 'qwen2.5:7b' COMMENT '模型名称',
    `model_provider` VARCHAR(32) DEFAULT 'ollama' COMMENT '模型提供商: ollama/openai/azure',
    `tools` JSON COMMENT '工具配置(JSON数组)',
    `knowledge_ids` JSON COMMENT '关联知识库ID数组',
    `skill_codes` JSON COMMENT '关联技能编码数组',
    `variables` JSON COMMENT '自定义变量(JSON)',
    `status` VARCHAR(16) DEFAULT 'DRAFT' COMMENT '状态: DRAFT-草稿/PUBLISHED-已发布',
    `tenant_code` VARCHAR(64) COMMENT '租户编码',
    `creator` VARCHAR(64) COMMENT '创建人',
    `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_app_code` (`app_code`),
    KEY `idx_tenant` (`tenant_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent应用配置';

-- 2. Agent应用版本表
CREATE TABLE IF NOT EXISTS `z_agent_app_version` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `app_code` VARCHAR(64) NOT NULL COMMENT '应用编码',
    `version` VARCHAR(32) NOT NULL COMMENT '版本号',
    `prompt` TEXT COMMENT '该版本的prompt快照',
    `model_name` VARCHAR(128) COMMENT '模型名称快照',
    `model_provider` VARCHAR(32) COMMENT '模型提供商快照',
    `tools` JSON COMMENT '工具配置快照',
    `knowledge_ids` JSON COMMENT '知识库ID快照',
    `skill_codes` JSON COMMENT '技能编码快照',
    `variables` JSON COMMENT '变量快照',
    `change_log` VARCHAR(512) COMMENT '版本变更日志',
    `status` VARCHAR(16) DEFAULT 'PUBLISHED' COMMENT '状态: PUBLISHED-已发布/DEPRECATED-已废弃',
    `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_app_version` (`app_code`, `version`),
    KEY `idx_app_code` (`app_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent应用版本';

-- 3. Agent实例表(他者复制后的运行时实例)
CREATE TABLE IF NOT EXISTS `z_agent_instance` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `instance_code` VARCHAR(64) NOT NULL COMMENT '实例编码(唯一标识)',
    `app_code` VARCHAR(64) NOT NULL COMMENT '来源应用编码',
    `app_version` VARCHAR(32) COMMENT '使用的应用版本',
    `instance_name` VARCHAR(128) COMMENT '实例名称(他者可自定义)',
    `owner_id` VARCHAR(64) COMMENT '实例所有者ID',
    `owner_name` VARCHAR(128) COMMENT '实例所有者名称',
    `config` JSON COMMENT '实例级配置覆盖(JSON)',
    `status` VARCHAR(16) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-活跃/FROZEN-冻结/DELETED-已删除',
    `visit_count` INT DEFAULT 0 COMMENT '访问次数',
    `last_visit_time` DATETIME COMMENT '最后访问时间',
    `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_instance_code` (`instance_code`),
    KEY `idx_app_code` (`app_code`),
    KEY `idx_owner` (`owner_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent实例';

-- 4. Agent分享表
CREATE TABLE IF NOT EXISTS `z_agent_share` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `share_code` VARCHAR(64) NOT NULL COMMENT '分享码(唯一标识,用于URL)',
    `instance_code` VARCHAR(64) NOT NULL COMMENT '关联实例编码',
    `app_code` VARCHAR(64) NOT NULL COMMENT '关联应用编码',
    `share_type` VARCHAR(16) DEFAULT 'LINK' COMMENT '分享类型: LINK-链接/CODE-提取码',
    `access_code` VARCHAR(32) COMMENT '访问密码(可选)',
    `expire_time` DATETIME COMMENT '过期时间(NULL表示永不过期)',
    `visit_count` INT DEFAULT 0 COMMENT '访问次数',
    `status` VARCHAR(16) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-有效/DISABLED-已禁用/EXPIRED-已过期',
    `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_share_code` (`share_code`),
    KEY `idx_instance` (`instance_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent分享';

-- 5. Agent会话表
CREATE TABLE IF NOT EXISTS `z_agent_conversation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `conversation_code` VARCHAR(64) NOT NULL COMMENT '会话编码',
    `instance_code` VARCHAR(64) NOT NULL COMMENT '关联实例编码',
    `user_id` VARCHAR(64) COMMENT '提问用户ID(可为空表示游客)',
    `user_name` VARCHAR(128) COMMENT '提问用户名称',
    `user_message` TEXT NOT NULL COMMENT '用户消息',
    `assistant_message` TEXT COMMENT '助手回复',
    `model_name` VARCHAR(128) COMMENT '使用的模型',
    `token_count` INT COMMENT '消耗token数',
    `tool_calls` JSON COMMENT '工具调用记录(JSON)',
    `latency_ms` INT COMMENT '响应延迟(毫秒)',
    `status` VARCHAR(16) DEFAULT 'SUCCESS' COMMENT '状态: SUCCESS-成功/FAILED-失败/TOOL_CALL-工具调用中',
    `error_msg` VARCHAR(512) COMMENT '错误信息',
    `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_instance` (`instance_code`),
    KEY `idx_conversation` (`conversation_code`),
    KEY `idx_user` (`user_id`),
    KEY `idx_create_time` (`gmt_create`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent会话';

-- 6. Agent应用草稿表(用于可视化编辑器)
CREATE TABLE IF NOT EXISTS `z_agent_app_draft` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `app_code` VARCHAR(64) NOT NULL COMMENT '应用编码',
    `draft_data` JSON NOT NULL COMMENT '草稿完整数据(JSON)',
    `version` INT DEFAULT 1 COMMENT '草稿版本号',
    `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_app_draft` (`app_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent应用草稿';
