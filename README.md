# Z-One Company 统一管理平台

## 项目简介

一人公司全栈统一管理平台，采用微服务架构，支持多模块管理。

## 技术栈

- **后端**: Spring Boot 2.7 + MyBatis Plus + MySQL
- **前端**: React + Ant Design + Vite
- **认证**: JWT Token
- **部署**: Docker + Nginx

## 模块说明

| 模块 | 说明 |
|------|------|
| z-ctc | 用户认证与权限管理 (TASK001) |
| z-config | 配置中心 |
| z-task | 任务中心 |
| z-workflow | 工作流 |
| z-schedule | 调度中心 |
| z-mist | 密钥管理 |
| z-meta | 元数据管理 |

## 快速开始

### Docker 部署

```bash
cd _build
docker-compose up -d
```

访问地址：
- 前端: http://localhost:8080
- 后端: http://localhost:8888

### 手动启动（推荐开发模式）

#### 1. 启动前端

```bash
cd z-one-company-frontend
npm install
npm run dev
```

前端地址: http://localhost:3000

#### 2. 启动后端

```bash
cd bootstraps/z-one-company-main-starter
mvn spring-boot:run
```

后端地址: http://localhost:8888

### 重新编译部署脚本

```bash
# Docker 模式
cd z-one-company-frontend
./rebuild.sh docker

# 手动模式
cd z-one-company-frontend
./rebuild.sh manual
```

## TASK001 用户认证模块

### 功能清单

- [x] 用户名密码登录
- [x] 用户名密码注册
- [x] 手机号验证码登录
- [x] 手机号注册
- [x] 邮箱注册
- [x] 发送验证码
- [x] 重置密码

### API 接口

| 接口 | 说明 |
|------|------|
| POST /auth/login | 用户名密码登录 |
| POST /auth/register/username | 用户名注册 |
| POST /auth/register/phone | 手机号注册 |
| POST /auth/register/email | 邮箱注册 |
| POST /auth/login/phone | 手机验证码登录 |
| POST /auth/register/send-code | 发送注册验证码 |
| POST /auth/reset-password/send-code | 发送重置密码验证码 |
| POST /auth/reset-password/phone | 手机找回密码 |
| POST /auth/reset-password/email | 邮箱找回密码 |

### 测试账号

- 用户名: admin
- 密码: 123456

### 前端页面

- 登录页: http://localhost:3000/ctc/login
- 概览页: http://localhost:3000/ctc/overview

## 数据库

远程数据库: 101.37.80.51:3306
数据库名: biz_service

### 核心表

- sys_user - 用户表
- sys_role - 角色表
- sys_permission - 权限表
- sys_verify_code - 验证码表
- sys_user_role - 用户角色关联
- sys_role_permission - 角色权限关联

## 开发说明

### 前端目录结构

```
z-one-company-frontend/
├── src/
│   ├── pages/
│   │   ├── ctc/          # 用户中心模块
│   │   │   ├── login/    # 登录页面
│   │   │   ├── Layout.jsx # 布局组件
│   │   │   └── overview/ # 概览页面
│   │   ├── config/       # 配置中心
│   │   ├── task/         # 任务中心
│   │   └── ...
│   ├── components/       # 公共组件
│   ├── services/         # API 请求
│   └── ...
└── dist/                 # 构建产物
```

### 后端目录结构

```
bootstraps/z-one-company-main-starter/
└── src/main/
    └── java/
        └── com/zifang/z/one/company/main/starter/
            └── ZCompanyMainStarter.java  # 启动类

z-ctc/z-ctc-web/src/main/java/
└── com/zifang/z/ctc/web/api/
    └── LoginController.java  # 认证接口

z-ctc/z-ctc-core/src/main/java/
└── com/zifang/ctc/core/
    ├── domain/entity/      # 实体类
    ├── domain/mapper/      # 数据访问
    └── service/impl/       # 业务实现
```

## 常见问题

### Docker 部署 nginx 无法访问

如果 Docker 部署时 nginx 无法绑定端口，请使用手动模式启动：

```bash
cd z-one-company-frontend
npm run dev
```

### 数据库连接失败

检查 application.yml 中的数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://101.37.80.51:3306/biz_service
    username: zifang
    password: Hhzemol!123
```

## 相关文档

- TASK001: `_feature/TASK001/技术系分.md`
- TASK002: `_feature/TASK002/Task.md`