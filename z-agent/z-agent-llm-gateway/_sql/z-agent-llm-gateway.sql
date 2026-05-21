-- =============================================
-- z-agent-llm-gateway 表结构
-- 表前缀: z_llm_usage_
-- =============================================

-- 1. LLM调用明细表
CREATE TABLE IF NOT EXISTS `z_llm_usage_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `trace_id` VARCHAR(64) NOT NULL COMMENT '调用追踪ID',
    `app_code` VARCHAR(64) COMMENT '应用编码',
    `instance_code` VARCHAR(64) COMMENT '实例编码',
    `user_id` VARCHAR(64) COMMENT '用户ID',
    `user_name` VARCHAR(128) COMMENT '用户名称',
    `provider_code` VARCHAR(32) NOT NULL COMMENT '供应商编码: ollama/openai/azure',
    `model_code` VARCHAR(64) NOT NULL COMMENT '模型编码',
    `input_tokens` INT DEFAULT 0 COMMENT '输入token数',
    `output_tokens` INT DEFAULT 0 COMMENT '输出token数',
    `total_tokens` INT DEFAULT 0 COMMENT '总token数',
    `latency_ms` INT DEFAULT 0 COMMENT '响应延迟(毫秒)',
    `status` VARCHAR(16) DEFAULT 'SUCCESS' COMMENT '状态: SUCCESS/FAILED',
    `error_msg` VARCHAR(512) COMMENT '错误信息',
    `conversation_code` VARCHAR(64) COMMENT '会话编码',
    `request_id` VARCHAR(128) COMMENT 'LLM返回的请求ID',
    `input_price` DECIMAL(10,6) DEFAULT 0 COMMENT '输入价格(元/千token)',
    `output_price` DECIMAL(10,6) DEFAULT 0 COMMENT '输出价格(元/千token)',
    `total_cost` DECIMAL(10,4) DEFAULT 0 COMMENT '本次调用费用(元)',
    `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_app_code` (`app_code`),
    KEY `idx_instance_code` (`instance_code`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_conversation` (`conversation_code`),
    KEY `idx_provider_model` (`provider_code`, `model_code`),
    KEY `idx_gmt_create` (`gmt_create`),
    KEY `idx_trace_id` (`trace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='LLM调用明细表';

-- 2. 日汇总表 (用于快速查询)
CREATE TABLE IF NOT EXISTS `z_llm_usage_daily` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `stat_date` DATE NOT NULL COMMENT '统计日期',
    `app_code` VARCHAR(64) COMMENT '应用编码',
    `user_id` VARCHAR(64) COMMENT '用户ID',
    `provider_code` VARCHAR(32) NOT NULL COMMENT '供应商编码',
    `model_code` VARCHAR(64) NOT NULL COMMENT '模型编码',
    `total_calls` INT DEFAULT 0 COMMENT '总调用次数',
    `total_input_tokens` BIGINT DEFAULT 0 COMMENT '总输入token',
    `total_output_tokens` BIGINT DEFAULT 0 COMMENT '总输出token',
    `total_tokens` BIGINT DEFAULT 0 COMMENT '总token',
    `total_cost` DECIMAL(10,4) DEFAULT 0 COMMENT '总费用(元)',
    `avg_latency_ms` INT DEFAULT 0 COMMENT '平均延迟',
    `success_calls` INT DEFAULT 0 COMMENT '成功次数',
    `failed_calls` INT DEFAULT 0 COMMENT '失败次数',
    `gmt_modified` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_date_app_user_provider_model` (`stat_date`, `app_code`, `user_id`, `provider_code`, `model_code`),
    KEY `idx_stat_date` (`stat_date`),
    KEY `idx_app_code` (`app_code`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='LLM日汇总表';

-- 3. 会话汇总表 (用于展示会话统计)
CREATE TABLE IF NOT EXISTS `z_llm_usage_conversation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `conversation_code` VARCHAR(64) NOT NULL COMMENT '会话编码',
    `app_code` VARCHAR(64) COMMENT '应用编码',
    `instance_code` VARCHAR(64) COMMENT '实例编码',
    `user_id` VARCHAR(64) COMMENT '用户ID',
    `user_name` VARCHAR(128) COMMENT '用户名称',
    `provider_code` VARCHAR(32) COMMENT '供应商编码',
    `model_code` VARCHAR(64) COMMENT '模型编码',
    `total_calls` INT DEFAULT 0 COMMENT '总调用次数',
    `total_input_tokens` BIGINT DEFAULT 0 COMMENT '总输入token',
    `total_output_tokens` BIGINT DEFAULT 0 COMMENT '总输出token',
    `total_tokens` BIGINT DEFAULT 0 COMMENT '总token',
    `total_cost` DECIMAL(10,4) DEFAULT 0 COMMENT '总费用',
    `first_message` TEXT COMMENT '首条用户消息',
    `last_message` TEXT COMMENT '末条用户消息',
    `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_conversation_code` (`conversation_code`),
    KEY `idx_app_code` (`app_code`),
    KEY `idx_instance_code` (`instance_code`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_gmt_create` (`gmt_create`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='LLM会话汇总表';
