-- ============================================
-- z-agent-skill-center 技能市场数据库
-- 数据库: biz_service (复用现有库)
-- 表前缀: z_skill_
-- ============================================

-- 1. 技能主表
CREATE TABLE IF NOT EXISTS `z_skill_skill` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `skill_code` VARCHAR(64) NOT NULL COMMENT '技能编码，唯一标识',
    `skill_name` VARCHAR(128) NOT NULL COMMENT '技能名称',
    `description` TEXT COMMENT '技能描述',
    `author` VARCHAR(64) DEFAULT 'unknown' COMMENT '发布者',
    `version` VARCHAR(32) DEFAULT '1.0.0' COMMENT '当前版本号',
    `category_code` VARCHAR(32) DEFAULT NULL COMMENT '分类编码',
    `tags` VARCHAR(256) DEFAULT NULL COMMENT '标签，逗号分隔',
    `icon_url` VARCHAR(256) DEFAULT NULL COMMENT '图标URL',
    `content` LONGTEXT COMMENT '技能内容(Markdown)',
    `status` VARCHAR(16) DEFAULT 'DRAFT' COMMENT '状态: DRAFT/PUBLISHED/ARCHIVED',
    `download_count` BIGINT DEFAULT 0 COMMENT '安装次数',
    `tenant_code` VARCHAR(32) DEFAULT 'admin' COMMENT '所属租户编码',
    `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted` INT DEFAULT 0 COMMENT '逻辑删除: 0正常 1删除',
    PRIMARY KEY (`id`),
    INDEX `idx_skill_code` (`skill_code`),
    INDEX `idx_category_code` (`category_code`),
    INDEX `idx_status` (`status`),
    INDEX `idx_tenant_code` (`tenant_code`),
    INDEX `idx_download_count` (`download_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能主表';

-- 2. 版本历史表
CREATE TABLE IF NOT EXISTS `z_skill_version` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `skill_code` VARCHAR(64) NOT NULL COMMENT '技能编码',
    `version` VARCHAR(32) NOT NULL COMMENT '版本号',
    `content` LONGTEXT COMMENT '版本内容',
    `change_log` TEXT COMMENT '变更说明',
    `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_skill_code` (`skill_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能版本历史';

-- 3. 分类表
CREATE TABLE IF NOT EXISTS `z_skill_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `cat_code` VARCHAR(32) NOT NULL COMMENT '分类编码',
    `cat_name` VARCHAR(64) NOT NULL COMMENT '分类名称',
    `parent_code` VARCHAR(32) DEFAULT NULL COMMENT '父分类编码',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_cat_code` (`cat_code`),
    INDEX `idx_parent_code` (`parent_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能分类';

-- 4. 安装记录表
CREATE TABLE IF NOT EXISTS `z_skill_install` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `skill_code` VARCHAR(64) NOT NULL COMMENT '技能编码',
    `installed_by` VARCHAR(64) DEFAULT 'anonymous' COMMENT '安装者',
    `tenant_code` VARCHAR(32) DEFAULT 'admin' COMMENT '所属租户编码',
    `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '安装时间',
    PRIMARY KEY (`id`),
    INDEX `idx_skill_code` (`skill_code`),
    INDEX `idx_installed_by` (`installed_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能安装记录';

-- ============================================
-- 种子数据：预设分类
-- ============================================
INSERT INTO `z_skill_category` (`cat_code`, `cat_name`, `parent_code`, `sort_order`) VALUES
('backend', '后端开发', NULL, 1),
('code-gen', '代码生成', 'backend', 11),
('data', '数据处理', 'backend', 12),
('api', 'API设计', 'backend', 13),
('frontend', '前端开发', NULL, 2),
('ui-component', 'UI组件', 'frontend', 21),
('page-builder', '页面生成', 'frontend', 22),
('devops', 'DevOps', NULL, 3),
('ci-cd', 'CI/CD', 'devops', 31),
('deploy', '部署', 'devops', 32),
('monitor', '监控', 'devops', 33),
('testing', '测试工具', NULL, 4),
('unit-test', '单元测试', 'testing', 41),
('e2e-test', '端到端测试', 'testing', 42),
('ai-agent', 'AI Agent', NULL, 5),
('prompt', 'Prompt工程', 'ai-agent', 51),
('workflow', '工作流', 'ai-agent', 52),
('doc', '文档写作', NULL, 6),
('readme', 'README生成', 'doc', 61),
('api-doc', 'API文档', 'doc', 62);

-- ============================================
-- 种子数据：示例技能
-- ============================================
INSERT INTO `z_skill_skill` (`skill_code`, `skill_name`, `description`, `author`, `version`, `category_code`, `tags`, `content`, `status`, `download_count`, `tenant_code`) VALUES
('java-entity-gen',
 'Java Entity 代码生成器',
 '根据建表SQL自动生成 Java Entity 类，支持 MyBatis-Plus 注解、Lombok 风格或手写 getter/setter',
 'zifang', '1.0.0', 'code-gen',
 'java,mybatis-plus,entity,代码生成',
 '# Java Entity 代码生成器\n\n## 功能\n根据 CREATE TABLE SQL 自动生成 Java Entity 类。\n\n## 使用方式\n将建表 SQL 粘贴进来，自动输出 Entity Java 代码。\n\n## 特性\n- 自动识别主键 @TableId\n- SQL类型→Java类型映射\n- 生成 @TableName 注解\n- 支持 Lombok 或手写 getter/setter\n\n## 输入示例\n```sql\nCREATE TABLE t_user (\n  id BIGINT AUTO_INCREMENT,\n  username VARCHAR(64),\n  email VARCHAR(128)\n);\n```\n\n## 输出示例\n```java\n@TableName("t_user")\npublic class User {\n    @TableId(type = IdType.AUTO)\n    private Long id;\n    private String username;\n    private String email;\n}\n```',
 'PUBLISHED', 128, 'admin'),

('react-crud-page',
 'React CRUD 页面生成器',
 '根据 Entity 字段自动生成 React + Ant Design ProTable CRUD 页面',
 'zifang', '1.2.0', 'page-builder',
 'react,ant-design,cruud,页面生成',
 '# React CRUD 页面生成器\n\n## 功能\n输入 Entity 字段定义，自动生成完整的 CRUD 前端页面。\n\n## 生成内容\n- ProTable 列表页\n- 新增/编辑 Modal 表单\n- 删除确认\n- 搜索筛选\n- 分页\n- api.ts 接口定义',
 'PUBLISHED', 89, 'admin'),

('spring-boot-api-scaffold',
 'Spring Boot API 脚手架',
 '一键生成 Spring Boot Controller + Service + Mapper 完整分层代码',
 'zifang', '2.0.0', 'api',
 'spring-boot,java,api,脚手架',
 '# Spring Boot API 脚手架\n\n## 功能\n输入表名和字段，自动生成：\n- Entity + Mapper (MyBatis-Plus)\n- IService + ServiceImpl\n- BizService + BizServiceImpl\n- Controller (CRUD)\n- Req / Resp / DTO\n- SQL 建表脚本',
 'PUBLISHED', 256, 'admin'),

('git-commit-msg',
 'Git Commit Message 规范助手',
 '根据代码diff自动生成符合 Conventional Commits 规范的提交信息',
 'zifang', '1.0.0', 'devops',
 'git,commit,conventional-commits',
 '# Git Commit Message 规范助手\n\n## 规则\n- feat: 新功能\n- fix: 修复\n- refactor: 重构\n- docs: 文档\n- style: 格式\n- test: 测试\n- chore: 构建/工具',
 'PUBLISHED', 45, 'admin');
