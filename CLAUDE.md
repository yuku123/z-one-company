# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**z-one-company** is a unified service platform for small teams ("一人公司统一管理平台"). It is a multi-module Maven project with Spring Boot backends and React frontends, deployed as an all-in-one system. It provides authentication (z-ctc), configuration management (z-config), task management (z-task), workflow/approval engine (z-wf), scheduling (z-schedule), secrets management (z-mist), metadata management (z-meta), object storage (z-oss), API gateway (z-gw), and message queue (z-mq).

## Technology Stack

- **Backend**: Java 8, Spring Boot 2.7.12, MyBatis-Plus 3.3.1, MySQL 8.0 (Druid connection pool)
- **Frontend**: React 19.2.4, Umi Max 4.6.45, Ant Design 6.3.3, Vite 5.1.4
- **API Docs**: Knife4j (Swagger UI at `/doc.html`)
- **Gateway**: Netty-based high-performance API gateway (z-gw)
- **Build**: Maven (backend), npm (frontend)

## Build Commands

### Backend (Maven)

```bash
# Full build with tests
mvn clean package

# Build without tests
mvn clean package -DskipTests

# Install to local repository
mvn clean install

# Start main app
cd bootstraps/z-one-company-main-starter && mvn spring-boot:run
```

### Frontend (npm)

```bash
# Main frontend
cd bootstraps/z-one-company-main-starter-frontend
npm install
npm run dev      # Development server on port 3000
npm run build    # Production build

# Individual module frontends (e.g., task)
cd z-task/z-task-frontend && npm install && npm run dev
```

### Docker

```bash
cd _build
docker-compose up -d
```

## Architecture

The project has three module categories:

- **bootstraps/** — Application entry points that package multiple services into deployable units
- **z-*/** — Core service modules, each providing a specific capability
- **z-boot/** — Shared Spring Boot starters (web, datasource) used across all services

The main application (`z-one-company-main-starter`) currently packages z-config and z-ctc together. Individual services can also be run standalone on their own ports.

## Module Index

| Module | Description |
|--------|-------------|
| z-ctc | User authentication & permissions (4A center) — login, register, password reset, JWT tokens |
| z-config | Configuration center — centralized configuration management |
| z-task | Task center — task creation, assignment, tracking |
| z-wf | Workflow engine (Approval center) — process definitions, instances, approval routing |
| z-schedule | Scheduling center — timed job scheduling |
| z-mist | Secrets/keys management |
| z-meta | Metadata management |
| z-oss | Object storage service |
| z-gw | API Gateway — Netty-based, high-performance routing |
| z-mq | Message queue system |
| z-rpc | RPC framework |
| z-ext | Extensions |
| z-boot/z-boot-starter | Shared Spring Boot starters (web, datasource) |
| z-boot/z-boot-dependencies | Bill of materials for dependency versions |
| bootstraps/z-one-company-main-starter | Main Spring Boot application (ports z-config, z-ctc together) |
| bootstraps/z-one-company-main-starter-frontend | Main React frontend |

## Key Entry Points

- **Main App**: `bootstraps/z-one-company-main-starter/src/main/java/com/zifang/z/one/company/main/starter/ZCompanyMainStarter.java`
- **Main Frontend**: `bootstraps/z-one-company-main-starter-frontend/src/main.jsx`
- **Workflow App**: `z-wf/z-wf-starter/src/main/java/com/zifang/z/wf/starter/ZWfApplication.java`
- **Gateway**: `z-gw/z-gw-starter/` (Netty-based)

## API Structure

Controllers follow `/api/{module}` paths (e.g., `/api/ctc/*`, `/api/config/*`, `/api/task/*`, `/api/wf/*`). The workflow module also exposes `/approval-center/*` endpoints.

API documentation via Knife4j: `http://localhost:8080/doc.html` (after starting the application).

## Database

- **Engine**: MySQL 8.0 with Druid connection pool
- **ORM**: MyBatis-Plus 3.3.1
- **Database name**: `biz_service`
- **Remote host**: 101.37.80.51:3306 (as configured in application.yml)
- **Schema files**: `_sql/` directory contains per-module `.sql` files (z-ctc.sql, z-wf.sql, z-task.sql, z-config.sql, etc.)

## Ports

| Service | Port |
|---------|------|
| Main app (backend) | 8080 |
| Main frontend (dev) | 3000 |
| z-config | 8848 |
| z-ctc | 8092 |
| z-wf | 8091 |
| z-task | 8090 |
| z-mist | 8085 |
| z-meta | 8093 |
