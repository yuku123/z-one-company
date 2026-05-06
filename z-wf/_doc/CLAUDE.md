# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Workflow Engine** project (z-wf) - a Spring Boot-based workflow management system. It is a multi-module Maven project with three modules:

- **z-wf-core**: Core workflow engine logic, entities, and services
- **z-wf-starter**: Spring Boot application entry point and configuration
- **z-wf-client**: Client library for external integration

## Technology Stack

- **Java 8**
- **Spring Boot 2.7.12**
- **MySQL 8.0** (with Druid connection pool)
- **MyBatis-Plus 3.3.1** (ORM framework)
- **Knife4j 3.0.3** (API documentation - Swagger UI available at `/doc.html`)
- **Lombok** (code generation)

## Common Commands

### Build

```bash
# Full build with tests
mvn clean package

# Build without tests (faster)
mvn clean package -DskipTests

# Install to local repository
mvn clean install
```

### Docker Deployment

```bash
# Build and deploy with Docker (includes packaging, image build, and container deployment)
sh build.sh

# The deployment:
# - Packages the application
# - Builds Docker image "z-wf:latest"
# - Deploys container on port 8081 (mapped to container port 8080)
# - Performs health check via /actuator/health endpoint
```

### Database

```bash
# Initialize database schema
mysql -u root -p < z-wf.sql
```

The `z-wf.sql` file contains the complete database schema including:
- Workflow definition tables
- Process instance tables
- Task/todo tables
- Audit log tables

## Project Structure

```
z-wf/
├── z-wf-core/              # Core module
│   └── src/main/java/com/zifang/workflow/
│       ├── context/        # Workflow context management
│       ├── dto/            # Data transfer objects
│       ├── entity/         # JPA/MyBatis entities
│       ├── enums/          # Enumerations
│       ├── extend/         # Extension points
│       ├── lock/           # Distributed locking
│       ├── service/        # Business services
│       ├── task/           # Task management
│       └── util/           # Utilities
├── z-wf-starter/           # Boot module
│   └── src/main/java/com/zifang/
│       └── WorkflowApplication.java  # Entry point
├── z-wf-client/            # Client module
│   └── src/main/java/com/zifang/
└── z-wf.sql                  # Database schema
```

## Configuration

The main configuration is in `z-wf-starter/src/main/resources/application.yml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/z_wf?useUnicode=true&characterEncoding=utf8
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

## Key Architectural Notes

1. **Module Dependencies**: The `z-wf-starter` module depends on both `z-wf-core` and `z-wf-client`. `z-wf-client` provides integration capabilities for external systems.

2. **Database**: The workflow engine uses MySQL with MyBatis-Plus for ORM operations. The `z-wf.sql` file contains the full schema including tables for workflow definitions, process instances, tasks, and audit logs.

3. **External Dependencies**: The project depends on internal `zifang` utility libraries (`util-core`, `util-http`, `util-jdbc`) which must be available in the local Maven repository or a configured remote repository.

4. **Health Checks**: The Docker deployment script performs health checks via Spring Boot Actuator's `/actuator/health` endpoint before completing the deployment.

## Approval Center API

The project provides a complete **Approval Center** REST API for frontend integration at `/approval-center`:

### API Endpoints

| No. | Endpoint | Description |
|-----|----------|-------------|
| 001 | `GET /approval-center/dashboard?userId=xxx` | Get dashboard statistics (todo/done/initiated counts) |
| 002 | `GET /approval-center/tasks/todo?userId=xxx` | Get todo task list (paginated) |
| 003 | `GET /approval-center/tasks/done?userId=xxx` | Get done task list (paginated) |
| 004 | `GET /approval-center/tasks/{taskId}` | Get task details (with form data, approval history) |
| 005 | `POST /approval-center/tasks/{taskId}/complete` | Complete task approval (approve/reject) |
| 006 | `GET /approval-center/my-processes?userId=xxx` | Get my initiated process list |
| 007 | `GET /approval-center/processes/{processInstanceId}` | Get process instance details (with approval trail) |
| 008 | `POST /approval-center/processes/start` | Start process instance |
| 009 | `GET /approval-center/processes/definitions` | Get available process definition list |
| 010 | `DELETE /approval-center/processes/{processInstanceId}` | Terminate/delete process instance |

### API Documentation

After starting the application, access the Swagger UI at:
- **Knife4j (Recommended)**: `http://localhost:8080/doc.html`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`

### DTO/VO Structure

All DTOs and VO objects are located in `z-wf-starter/src/main/java/com/zifang/z/wf/starter/dto/`:
- `ApprovalRequestDTO` - Approval request body
- `DashboardStatsVO` - Dashboard statistics
- `PageResult<T>` - Paginated response wrapper
- `ProcessDetailVO` - Process instance details with approval trail
- `ProcessInstanceVO` - Process instance summary
- `StartProcessRequestDTO` - Start process request body
- `TaskDetailVO` - Task details with form data and history
- `TaskSummaryVO` - Task summary for list views