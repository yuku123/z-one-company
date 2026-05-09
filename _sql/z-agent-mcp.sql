-- MCP 服务配置管理表
CREATE TABLE IF NOT EXISTS `z_mcp_server` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `server_name` VARCHAR(100) NOT NULL COMMENT '服务名称',
  `transport_type` VARCHAR(20) NOT NULL DEFAULT 'HTTP' COMMENT '传输类型: HTTP/STDIO',
  `url` VARCHAR(500) COMMENT 'HTTP端点URL',
  `command` VARCHAR(500) COMMENT '启动命令(STDIO)',
  `args` VARCHAR(1000) COMMENT '命令参数(JSON数组)',
  `auth_token` VARCHAR(500) COMMENT '认证Token',
  `timeout` INT DEFAULT 60 COMMENT '超时秒数',
  `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态: active/inactive',
  `tenant_code` VARCHAR(50) DEFAULT 'default' COMMENT '租户编码',
  `domain_code` VARCHAR(50) COMMENT '域编码',
  `remark` VARCHAR(500) COMMENT '备注',
  `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_tenant` (`tenant_code`),
  INDEX `idx_server_name` (`server_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MCP服务配置';
