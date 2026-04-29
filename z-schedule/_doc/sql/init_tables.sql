-- Z-Job 数据库初始化脚本
-- 数据库: MySQL 5.7+
-- 字符集: utf8mb4

-- 1. 任务信息表
CREATE TABLE IF NOT EXISTS `job_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_group` int(11) NOT NULL COMMENT '执行器主键ID',
  `job_cron` varchar(128) NOT NULL COMMENT '任务执行CRON',
  `job_desc` varchar(255) NOT NULL COMMENT '任务描述',
  `add_time` datetime DEFAULT NULL COMMENT '添加时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `author` varchar(64) DEFAULT NULL COMMENT '作者',
  `alarm_email` varchar(255) DEFAULT NULL COMMENT '报警邮件',
  `executor_route_strategy` varchar(50) DEFAULT NULL COMMENT '执行器路由策略',
  `executor_handler` varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
  `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
  `executor_block_strategy` varchar(50) DEFAULT NULL COMMENT '阻塞处理策略',
  `executor_timeout` int(11) NOT NULL DEFAULT '0' COMMENT '任务执行超时时间，单位秒',
  `executor_fail_retry_count` int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
  `log_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '调度日志主键',
  `trigger_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '调度状态：0-停止，1-运行',
  `trigger_last_time` bigint(13) NOT NULL DEFAULT '0' COMMENT '上次调度时间',
  `trigger_next_time` bigint(13) NOT NULL DEFAULT '0' COMMENT '下次调度时间',
  PRIMARY KEY (`id`),
  KEY `idx_trigger_status_next_time` (`trigger_status`,`trigger_next_time`) USING BTREE,
  KEY `idx_job_group` (`job_group`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务信息表';

-- 2. 任务调度日志表
CREATE TABLE IF NOT EXISTS `job_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_group` int(11) NOT NULL COMMENT '执行器主键ID',
  `job_id` int(11) NOT NULL COMMENT '任务ID',
  `executor_address` varchar(255) DEFAULT NULL COMMENT '执行器地址',
  `executor_handler` varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
  `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
  `executor_sharding_param` varchar(20) DEFAULT NULL COMMENT '执行器分片参数',
  `executor_fail_retry_count` int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
  `trigger_time` datetime DEFAULT NULL COMMENT '调度时间',
  `trigger_code` int(11) NOT NULL COMMENT '调度结果代码',
  `trigger_msg` text COMMENT '调度日志',
  `handle_time` datetime DEFAULT NULL COMMENT '执行时间',
  `handle_code` int(11) NOT NULL COMMENT '执行状态代码',
  `handle_msg` text COMMENT '执行日志',
  `alarm_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败',
  PRIMARY KEY (`id`),
  KEY `idx_trigger_time` (`trigger_time`) USING BTREE,
  KEY `idx_job_id` (`job_id`,`trigger_time`) USING BTREE,
  KEY `idx_handle_code` (`handle_code`,`alarm_status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务调度日志表';

-- 3. 执行器注册表
CREATE TABLE IF NOT EXISTS `job_registry` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `registry_group` varchar(50) NOT NULL COMMENT '注册分组',
  `registry_key` varchar(255) NOT NULL COMMENT '注册Key',
  `registry_value` varchar(255) NOT NULL COMMENT '注册Value',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_registry_key_value` (`registry_key`,`registry_value`) USING BTREE,
  KEY `idx_update_time` (`update_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执行器注册表';

-- 4. 执行器分组表
CREATE TABLE IF NOT EXISTS `job_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_name` varchar(64) NOT NULL COMMENT '执行器AppName',
  `title` varchar(12) NOT NULL COMMENT '执行器名称',
  `order` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `address_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '执行器地址类型：0=自动注册、1=手动录入',
  `address_list` varchar(512) DEFAULT NULL COMMENT '执行器地址列表',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执行器分组表';

-- 5. 分布式锁表
CREATE TABLE IF NOT EXISTS `job_lock` (
  `name` varchar(64) NOT NULL COMMENT '锁名称',
  `owner` varchar(255) NOT NULL COMMENT '锁持有者',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`name`),
  KEY `idx_expire_time` (`expire_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分布式锁表';

-- 初始化数据
INSERT INTO `job_group` (`app_name`, `title`, `order`, `address_type`, `address_list`, `update_time`)
VALUES ('z-schedule-executor', '示例执行器', 1, 0, NULL, NOW());
