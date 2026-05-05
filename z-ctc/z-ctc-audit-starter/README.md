# z-ctc-audit-starter 接入文档

## 概述

`z-ctc-audit-starter` 是 CTC 模块的审计客户端，其他应用（如 z-task、z-wf）引入后，只需在方法上加 `@Audit` 注解，即可自动将操作日志上报给 CTC 集中存储。

## 接入步骤

### 1. pom.xml 引入依赖

```xml
<dependency>
    <groupId>com.zifang</groupId>
    <artifactId>z-ctc-audit-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. application.yml 配置

```yaml
audit:
  enabled: true
  # 应用名称，必填，上报事件时标识来源
  application: z-task
  # CTC 服务地址
  ctc:
    url: http://localhost:8080
```

### 3. 启动类添加注解

```java
@SpringBootApplication
@EnableAsync  // 必须，否则上报是同步的，会影响业务接口性能
public class ZTaskApplication {}
```

### 4. 方法添加 @Audit 注解

```java
@Audit("创建任务")
@PostMapping("/api/task")
public Long create(@RequestBody @Valid TaskReq req) {
    return taskBizService.create(req);
}

@Audit("删除任务")
@PostMapping("/api/task/delete")
public void delete(@RequestBody IdReq req) {
    taskBizService.delete(req.getId());
}
```

## 上报内容

| 字段 | 说明 |
|------|------|
| traceId | UUID，每次请求唯一 |
| application | 应用名，来自配置 |
| operationType | GET=QUERY / POST=CREATE / PUT=PATCH=UPDATE / DELETE=DELETE |
| operationDesc | @Audit 注解值，未填则用 类名.方法名 |
| userId / userName | 从 JWT token 中解析 |
| tenantCode | 从 JWT token 中解析 |
| ipAddress | 客户端 IP |
| userAgent | 浏览器标识 |
| requestUrl | 请求路径 |
| requestMethod | HTTP 方法 |
| requestParams | body 参数 JSON |
| executionTime | 执行时长(ms) |
| status | 1=成功，0=失败 |
| errorMsg | 失败时错误信息 |
| timestamp | 时间戳 |

## CTC 查询接口

审计事件上报后，可在 CTC 管理端查询：

```
GET  /api/audit/log          → 分页查询审计日志
GET  /api/audit/export       → 导出 CSV
POST /api/audit/event        → 接收上报事件（其他应用不可调用）
```

## 注意事项

- 必须在启动类加 `@EnableAsync`，否则审计上报会阻塞业务线程
- JWT secret 与 CTC 保持一致（`zifang-ctc-secret-key`），用于解析 userId/userName
- 建议只对写操作（增删改）加 `@Audit`，查询一般不加
- `audit.enabled: false` 可禁用审计，不影响业务
