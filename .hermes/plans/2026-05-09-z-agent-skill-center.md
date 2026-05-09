# z-agent-skill-center 技能市场 实现计划

> **For Hermes:** 按 task 逐一实现，使用 delegate_task 并行处理独立模块。

**Goal:** 实现一个"技能市场"(Skill Marketplace)，类似 npm registry / 在线 skill 提供商的玩法。用户可以发布、浏览、搜索、安装 AI Agent 技能。

**Architecture:** 标准 z-one-company 全栈模式：Spring Boot (Java 8) + MyBatis-Plus + React + Ant Design。模块拆分为 z-agent-skill-core (实体/服务层) + z-agent-skill-web (Controller层)，集成到 main-starter 全量部署。

**Tech Stack:** Java 8, Spring Boot 2.7.12, MyBatis-Plus 3.3.1, MySQL 8.0, React 19, Ant Design 6.3.3

---

## 数据模型

### 4 张表 (z_skill_* 前缀)

#### z_skill_skill — 技能主表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT AUTO_INCREMENT PK | 主键 |
| skill_code | VARCHAR(64) NOT NULL INDEX | 业务标识，如 "java-code-gen" |
| skill_name | VARCHAR(128) | 显示名称 |
| description | TEXT | 描述 |
| author | VARCHAR(64) | 发布者 |
| version | VARCHAR(32) | 当前版本号 如 "1.0.0" |
| category_code | VARCHAR(32) | 分类编码 |
| tags | VARCHAR(256) | 逗号分隔标签 |
| icon_url | VARCHAR(256) | 图标URL |
| content | LONGTEXT | 技能内容 (Markdown) |
| status | VARCHAR(16) | PUBLISHED / DRAFT / ARCHIVED |
| download_count | BIGINT DEFAULT 0 | 安装次数 |
| tenant_code | VARCHAR(32) | 所属租户 |
| gmt_create | DATETIME | 创建时间 |
| gmt_modified | DATETIME | 修改时间 |
| is_deleted | INT DEFAULT 0 | 逻辑删除 |

#### z_skill_version — 版本历史
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT AUTO_INCREMENT PK | 主键 |
| skill_code | VARCHAR(64) INDEX | 关联技能 |
| version | VARCHAR(32) | 版本号 |
| content | LONGTEXT | 该版本内容 |
| change_log | TEXT | 变更说明 |
| gmt_create | DATETIME | 创建时间 |

#### z_skill_category — 分类
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT AUTO_INCREMENT PK | 主键 |
| cat_code | VARCHAR(32) UNIQUE INDEX | 分类编码 |
| cat_name | VARCHAR(64) | 分类名称 |
| parent_code | VARCHAR(32) | 父分类编码 |
| sort_order | INT DEFAULT 0 | 排序 |
| gmt_create | DATETIME | 创建时间 |

#### z_skill_install — 安装记录
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT AUTO_INCREMENT PK | 主键 |
| skill_code | VARCHAR(64) INDEX | 关联技能 |
| installed_by | VARCHAR(64) | 安装者 |
| tenant_code | VARCHAR(32) | 所属租户 |
| gmt_create | DATETIME | 安装时间 |

---

## 后端模块结构

```
z-agent/z-agent-skill-center/
├── pom.xml                          (parent, packaging=pom)
├── z-agent-skill-core/
│   ├── pom.xml
│   └── src/main/java/com/zifang/z/agent/skill/core/
│       ├── domain/
│       │   ├── entity/Skill.java, SkillVersion.java, SkillCategory.java, SkillInstall.java
│       │   ├── mapper/SkillMapper.java, SkillVersionMapper.java, ...
│       │   └── service/ISkillService.java, ISkillVersionService.java, ...
│       │       └── impl/SkillServiceImpl.java, ...
│       ├── service/
│       │   ├── SkillBizService.java
│       │   ├── SkillCategoryBizService.java
│       │   └── impl/SkillBizServiceImpl.java, SkillCategoryBizServiceImpl.java
│       ├── service/dto/
│       │   └── SkillDTO.java, SkillVersionDTO.java, SkillCategoryDTO.java
│       └── common/
│           └── exception/BusinessException.java
│           └── result/ResultCode.java
├── z-agent-skill-web/
│   ├── pom.xml
│   └── src/main/java/com/zifang/z/agent/skill/web/
│       ├── api/
│       │   ├── SkillController.java
│       │   ├── SkillCategoryController.java
│       │   ├── request/SkillReq.java, SkillPageReq.java, SkillVersionReq.java, SkillCategoryReq.java
│       │   └── response/SkillResp.java, SkillVersionResp.java, SkillCategoryResp.java
│       └── config/
│           └── DataSourceConfigForSkill.java   (SqlSessionFactory Bean for skill module)
└── z-agent-skill-starter/
    ├── pom.xml
    └── src/main/java/com/zifang/z/agent/skill/starter/
        └── (auto-config, 如果独立部署用)
```

## API 设计

所有接口前缀 `/api/skill`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/skill/page | 分页搜索 (keyword, categoryCode, tags, status, sortBy) |
| GET | /api/skill/get?skillCode=xxx | 获取技能详情 |
| GET | /api/skill/content?skillCode=xxx | 获取技能内容 (Markdown) |
| POST | /api/skill | 创建技能 (草稿) |
| POST | /api/skill/update | 更新技能 |
| POST | /api/skill/{id}/delete | 删除技能 |
| POST | /api/skill/publish | 发布技能 (草稿→已发布) |
| POST | /api/skill/install | 安装技能 (download_count+1 + 记录) |
| GET | /api/skill/versions?skillCode=xxx | 版本历史 |
| POST | /api/skill/version | 新增版本 |
| GET | /api/skill/category/tree | 分类树 |
| POST | /api/skill/category | 新增分类 |
| GET | /api/skill/stats | 统计 (总数/热门/最新) |
| GET | /api/skill/hot | 热门技能 Top N |

---

## 前端页面设计

### 技能市场主页面 (`pages/skill/index.jsx`)

布局：
```
┌─────────────────────────────────────────────┐
│  🔍 搜索技能...    [分类▼] [排序: 热门▼]  [+ 发布技能] │
├──────────┬──────────────────────────────────┤
│ 分类树   │  ┌─────┐ ┌─────┐ ┌─────┐       │
│          │  │Skill│ │Skill│ │Skill│       │
│ ·代码生成│  │Card │ │Card │ │Card │       │
│ ·数据处理│  └─────┘ └─────┘ └─────┘       │
│ ·测试工具│  ┌─────┐ ┌─────┐ ┌─────┐       │
│ ·DevOps │  │Skill│ │Skill│ │Skill│       │
│          │  │Card │ │Card │ │Card │       │
│          │  └─────┘ └─────┘ └─────┘       │
│          │          [分页]                 │
└──────────┴──────────────────────────────────┘
```

### Skill Card 设计
- 图标 + 名称
- 简短描述 (2行截断)
- 标签 (Tag)
- 版本号 + 作者
- 下载量 + 安装按钮
- 点击卡片 → Drawer 展示详情 (content 渲染为 Markdown)

### 发布技能 (Modal)
- 表单: skillCode, skillName, description, category, tags, content (Markdown 编辑器)
- 保存为 DRAFT，后续可 PUBLISH

### 路由注册
- main.jsx: `<Route path="skill/*" element={<SkillIndex />} />`
- pages/skill/index.jsx: `<Route path="market" element={<SkillMarket />} />`
- App.jsx: 菜单项 "技能市场" → /skill/market

---

## 集成点

1. **pom.xml**: z-one-company-main-starter 添加 z-agent-skill-web 依赖
2. **DataSourceConfig**: z-agent-skill-web 中创建 DataSourceConfigForSkill (参考 DataSourceConfigForCtc)
3. **SQL**: _sql/z-skill.sql 建表脚本
4. **前端路由**: main.jsx + pages/skill/index.jsx + App.jsx 菜单
5. **api.ts**: 新增 skillApi 模块

---

## Task List

### Task 1: 创建 SQL 建表脚本 _sql/z-skill.sql
4张表完整 DDL + 种子数据 (预设分类)

### Task 2: 初始化 z-agent-skill-core 模块
改写 pom.xml (添加 MyBatis-Plus/Spring Boot 依赖) + 创建4个 Entity + 4个 Mapper + 4个 domain.Service + DTO

### Task 3: 实现 z-agent-skill-core 业务层
SkillBizService + SkillCategoryBizService + impl

### Task 4: 初始化 z-agent-skill-web 模块
改写 pom.xml + 创建 Req/Resp + Controller + DataSourceConfig

### Task 5: 集成到 main-starter
pom.xml 添加依赖 + 注册 DataSource

### Task 6: 前端 Skills 页面 + api.ts
Skill Card 组件 + 市场页面 + Category 树 + 搜索 + 安装功能 + 发布 Modal

### Task 7: 前端路由 + 菜单注册
main.jsx + pages/skill/index.jsx + App.jsx

### Task 8: 编译验证
mvn compile + npm run build
