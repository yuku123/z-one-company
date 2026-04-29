-- TASK001: 验证码表 (sys_verify_code)
-- 数据库: biz_service

USE biz_service;

-- 创建验证码表
CREATE TABLE IF NOT EXISTS sys_verify_code (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    biz_type VARCHAR(32) NOT NULL COMMENT '业务类型: REGISTER-注册, LOGIN-登录, RESET_PWD-重置密码',
    code_type VARCHAR(32) NOT NULL COMMENT '验证码类型: PHONE-手机, EMAIL-邮箱',
    receiver VARCHAR(128) NOT NULL COMMENT '接收者(手机号/邮箱)',
    code VARCHAR(16) NOT NULL COMMENT '验证码',
    expire_time DATETIME NOT NULL COMMENT '过期时间',
    used TINYINT NOT NULL DEFAULT '0' COMMENT '是否已使用: 0-否, 1-是',
    gmt_create DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_receiver_expire (receiver, expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验证码记录表';