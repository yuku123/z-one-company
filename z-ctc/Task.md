# zb-ctc 任务清单

## 项目概述

**项目名称**: zb-ctc (Comprehensive-Tissue-Centre)
**项目目标**: 完成 4A + SSO (Authentication, Authorization, Accounting, Audit + Single Sign-On) 统一身份认证平台

## 技术栈

### 后端
- **框架**: Spring Boot 2.7.12
- **构建工具**: Maven (多模块)
- **ORM**: MyBatis-Plus 3.3.1
- **数据库**: MySQL 8.0+
- **缓存**: Redis
- **安全**: Spring Security + JWT

### 前端
- **框架**: React 18 + Umi 4.x
- **UI 库**: Ant Design 5.x + ProComponents

## 核心实体

### 1. 用户 (User)
- id, userName, password, realName, email, phone, avatar
- status, tenantCode, deptId
- lastLoginTime, lastLoginIp
- gmtCreate, gmtModified

### 2. 角色 (Role)
- id, roleCode, roleName, description
- status, tenantCode
- gmtCreate, gmtModified

### 3. 权限 (Permission)
- id, permCode, permName, permType (MENU/BUTTON/API)
- parentId, path, sortOrder
- status, tenantCode
- gmtCreate, gmtModified

### 4. 租户 (Tenant) - tenant_code 作为主键
- tenantCode (PK), tenantName
- contactName, contactPhone, contactEmail
- status, expireTime
- gmtCreate, gmtModified

### 5. 关联表
- user_role: user_id, role_id
- role_permission: role_id, permission_id

### 6. 审计日志 (AuditLog)
- operatorId, operatorName, operationType
- targetType, targetId, description
- beforeValue, afterValue
- ipAddress, userAgent, requestUrl, requestMethod

## 任务列表

### 阶段 1: 项目初始化 [已完成]
- [x] 创建 Maven 多模块项目结构
- [x] 配置 Spring Boot 2.7.12
- [x] 配置 MyBatis-Plus
- [x] 配置数据库连接池 (HikariCP)
- [x] 配置 Redis
- [x] 配置 JWT
- [x] 配置跨域 (CORS)

### 阶段 2: 核心实体与 Mapper [已完成]
- [x] 创建 User 实体
- [x] 创建 Role 实体
- [x] 创建 Permission 实体
- [x] 创建 Tenant 实体 (tenant_code 作为主键)
- [x] 创建 UserRole 关联实体
- [x] 创建 RolePermission 关联实体
- [x] 创建 AuditLog 实体
- [x] 创建所有 Mapper 接口
- [x] 创建所有 Mapper XML 文件

### 阶段 3: 将 tenant_id 改为 tenant_code [已完成]
- [x] 修改 User 实体：tenantId -> tenantCode (String)
- [x] 修改 Role 实体：tenantId -> tenantCode (String)
- [x] 修改 Permission 实体：tenantId -> tenantCode (String)
- [x] 修改 Tenant 实体：tenantCode 作为主键 (IdType.INPUT)
- [x] 更新所有 Mapper XML 文件
- [x] 更新数据库 schema 脚本

### 阶段 4: Service 层 [已完成]
- [x] 创建 UserService 接口和实现
- [x] 实现用户 CRUD 操作
- [x] 实现用户角色分配
- [x] 实现密码重置

### 阶段 5: Controller 层 [部分完成]
- [ ] 创建 AuthController (登录/登出/刷新Token)
- [ ] 创建 UserController
- [ ] 创建 RoleController
- [ ] 创建 PermissionController
- [ ] 创建 TenantController
- [ ] 创建 AuditLogController

### 阶段 6: 前端开发 [已完成基础结构]
- [x] 创建 Umi 项目结构
- [x] 配置路由和布局
- [x] 创建登录页面
- [x] 创建 Dashboard 页面
- [x] 创建用户管理页面
- [x] 创建角色管理页面
- [x] 创建权限管理页面
- [x] 创建审计日志页面
- [x] 实现 API 服务层
- [ ] 实现权限控制组件

### 阶段 7: Docker 支持 [已完成]
- [x] 创建 Dockerfile
- [x] 创建 docker-compose.yml
- [x] 配置 MySQL 容器
- [x] 配置 Redis 容器
- [x] 配置应用容器

### 阶段 8: 测试与优化 [待完成]
- [ ] 编写单元测试
- [ ] 编写集成测试
- [ ] 性能测试
- [ ] 安全测试

### 阶段 9: 文档 [已完成基础文档]
- [x] 编写 README.md
- [x] 编写 CLAUDE.md
- [ ] 编写 API 文档
- [ ] 编写部署文档

## 当前状态

**完成度**: ~75%

**已完成模块**:
- 项目基础架构
- 核心实体和数据库设计
- tenant_id -> tenant_code 迁移
- Mapper 层
- Service 层（UserService）
- 前端基础结构和页面
- Docker 配置
- 基础文档

**待完成模块**:
- Controller 层（API 实现）
- 前端 API 联调
- 权限控制组件
- 测试用例
- 完整文档

## 下一步计划

1. 完成所有 Controller 层的实现
2. 前后端 API 联调
3. 实现前端权限控制
4. 编写测试用例
5. 完善文档

## 注意事项

1. **tenant_code**: 所有实体都使用 `tenant_code` (String) 进行多租户数据隔离
2. **密码安全**: 生产环境必须更换 JWT 密钥和默认密码
3. **数据库**: 确保 MySQL 使用 utf8mb4 字符集
4. **时区**: 建议统一使用 Asia/Shanghai 时区

## 联系方式

如有问题或建议，请提交 Issue 或 Pull Request。
