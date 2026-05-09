-- z-ops 镜像仓库相关表
-- Image 镜像
CREATE TABLE z_ops_image (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(128) NOT NULL COMMENT '镜像名',
  registry VARCHAR(256) DEFAULT '' COMMENT '仓库地址',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='镜像表';

-- ImageTag 镜像版本
CREATE TABLE z_ops_image_tag (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  image_id BIGINT NOT NULL COMMENT '镜像ID',
  tag VARCHAR(64) NOT NULL COMMENT '版本标签',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_image_tag (image_id, tag),
  KEY idx_image_id (image_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='镜像版本表';

-- ImageBuild 构建记录
CREATE TABLE z_ops_image_build (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  image_name VARCHAR(128) NOT NULL COMMENT '镜像名',
  tag VARCHAR(64) NOT NULL COMMENT '版本',
  app_name VARCHAR(128) DEFAULT '' COMMENT '应用名',
  branch VARCHAR(128) DEFAULT '' COMMENT '分支',
  env VARCHAR(32) DEFAULT '' COMMENT '环境',
  status VARCHAR(32) DEFAULT 'building' COMMENT '状态 building/success/failed',
  image_tag VARCHAR(256) DEFAULT '' COMMENT '完整镜像标签',
  build_log TEXT COMMENT '构建日志',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='镜像构建记录表';
