-- =============================================
-- z-agent-llm-center 表结构
-- 表前缀: z_llm_
-- =============================================

-- 1. LLM供应商表
CREATE TABLE IF NOT EXISTS `z_llm_provider` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `provider_code` VARCHAR(32) NOT NULL COMMENT '供应商编码: ollama/openai/azure/claude/gemini',
    `provider_name` VARCHAR(128) NOT NULL COMMENT '供应商名称',
    `base_url` VARCHAR(256) COMMENT 'API地址',
    `api_key` VARCHAR(512) COMMENT 'API密钥(加密存储)',
    `enabled` TINYINT DEFAULT 1 COMMENT '是否启用',
    `priority` INT DEFAULT 100 COMMENT '优先级(越小越优先)',
    `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_provider_code` (`provider_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='LLM供应商配置';

-- 2. LLM模型表
CREATE TABLE IF NOT EXISTS `z_llm_model` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `model_code` VARCHAR(64) NOT NULL COMMENT '模型编码',
    `model_name` VARCHAR(128) NOT NULL COMMENT '模型名称(展示用)',
    `provider_code` VARCHAR(32) NOT NULL COMMENT '所属供应商编码',
    `model_type` VARCHAR(32) DEFAULT 'chat' COMMENT '模型类型: chat/completion/embedding',
    `context_window` INT COMMENT '上下文窗口(token)',
    `supports_function_call` TINYINT DEFAULT 0 COMMENT '是否支持函数调用',
    `supports_vision` TINYINT DEFAULT 0 COMMENT '是否支持视觉',
    `max_output_tokens` INT COMMENT '最大输出token',
    `input_price` DECIMAL(10,6) COMMENT '输入价格(元/千token)',
    `output_price` DECIMAL(10,6) COMMENT '输出价格(元/千token)',
    `enabled` TINYINT DEFAULT 1 COMMENT '是否启用',
    `description` VARCHAR(512) COMMENT '模型描述',
    `default_params` JSON COMMENT '默认参数配置',
    `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_model_code` (`model_code`),
    KEY `idx_provider` (`provider_code`),
    KEY `idx_enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='LLM模型配置';

-- 3. Agent工具模板表
CREATE TABLE IF NOT EXISTS `z_agent_tool_template` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `tool_code` VARCHAR(64) NOT NULL COMMENT '工具编码',
    `tool_name` VARCHAR(128) NOT NULL COMMENT '工具名称',
    `tool_type` VARCHAR(32) DEFAULT 'function' COMMENT '工具类型: function/http/search',
    `description` VARCHAR(512) COMMENT '工具描述',
    `icon` VARCHAR(64) COMMENT '图标',
    `definition` JSON NOT NULL COMMENT '工具定义(JSON Schema)',
    `endpoint` VARCHAR(256) COMMENT 'HTTP工具调用地址',
    `enabled` TINYINT DEFAULT 1 COMMENT '是否启用',
    `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tool_code` (`tool_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent工具模板';

-- 初始化默认工具模板
INSERT INTO `z_agent_tool_template` (`tool_code`, `tool_name`, `tool_type`, `description`, `definition`) VALUES
('weather', '天气查询', 'function', '查询指定城市的天气信息', '{"type":"object","properties":{"city":{"type":"string","description":"城市名称"}},"required":["city"]}'),
('calculator', '计算器', 'function', '执行数学计算', '{"type":"object","properties":{"expression":{"type":"string","description":"数学表达式"}},"required":["expression"]}'),
('web_search', '网页搜索', 'http', '搜索互联网信息', '{"type":"object","properties":{"query":{"type":"string","description":"搜索关键词"},"count":{"type":"integer","description":"返回数量"}},"required":["query"]}'),
('code_executor', '代码执行', 'function', '执行代码片段', '{"type":"object","properties":{"language":{"type":"string","description":"编程语言"},"code":{"type":"string","description":"代码内容"}},"required":["language","code"]}');

-- 初始化默认Provider
INSERT INTO `z_llm_provider` (`provider_code`, `provider_name`, `base_url`, `enabled`, `priority`) VALUES
('ollama', 'Ollama (本地)', 'http://localhost:11434', 1, 10),
('openai', 'OpenAI', 'https://api.openai.com/v1', 1, 20),
('azure', 'Azure OpenAI', '', 0, 30);

-- 初始化默认模型
INSERT INTO `z_llm_model` (`model_code`, `model_name`, `provider_code`, `model_type`, `context_window`, `supports_function_call`, `enabled`, `description`) VALUES
('qwen2.5:7b', 'Qwen 2.5 7B', 'ollama', 'chat', 8192, 0, 1, '通义千问2.5 7B参数版本'),
('qwen2.5:14b', 'Qwen 2.5 14B', 'ollama', 'chat', 8192, 0, 1, '通义千问2.5 14B参数版本'),
('llama3:8b', 'Llama 3 8B', 'ollama', 'chat', 8192, 0, 1, 'Meta Llama 3 8B'),
('gpt-4o', 'GPT-4o', 'openai', 'chat', 128000, 1, 1, 'OpenAI最新多模态模型'),
('gpt-4o-mini', 'GPT-4o Mini', 'openai', 'chat', 128000, 1, 1, '轻量版GPT-4o'),
('gpt-4-turbo', 'GPT-4 Turbo', 'openai', 'chat', 128000, 1, 1, 'GPT-4 turbo版本');
