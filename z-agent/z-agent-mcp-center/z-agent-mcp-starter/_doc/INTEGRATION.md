# z-agent-mcp-starter 集成指南

> 将任意 Spring Boot 服务的业务方法暴露为 MCP 工具，供 AI Agent 调用。

---

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.zifang</groupId>
    <artifactId>z-agent-mcp-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. 注解业务方法

```java
@Service
public class ConfigService {

    @McpTool(name = "get_config", description = "获取配置值")
    public String getConfig(
        @McpParam(name = "key", description = "配置键", required = true) String key
    ) {
        return configMap.getOrDefault(key, "not found");
    }

    @McpTool(name = "list_configs", description = "列出所有配置项")
    public List<String> listConfigs() {
        return new ArrayList<>(configMap.keySet());
    }
}
```

### 3. 确保组件扫描覆盖 starter 包

```java
@SpringBootApplication(scanBasePackages = {
    "com.your.package",           // 你的业务包
    "com.zifang.z.agent.mcp.starter"  // starter 自动配置
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 4. 启动并验证

```bash
# 启动服务
java -jar your-app.jar

# 验证 MCP 端点
curl -X POST http://localhost:8080/mcp \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","id":1,"method":"tools/list","params":{}}'
```

返回的工具列表会自动包含你的 `get_config` 和 `list_configs`。

---

## @McpTool 注解详解

标记方法为 MCP 工具。**一个 Service 可以有多个 @McpTool 方法。**

```java
@McpTool(
    name = "tool_name",        // 工具名（默认取方法名）
    description = "工具描述"    // Agent 根据描述决定何时调用
)
```

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| name | String | 方法名 | 工具唯一标识，Agent 用此名称调用 |
| description | String | "Auto-registered tool: xxx" | **重要：** Agent 根据描述匹配用户意图 |

**最佳实践：**
- `name` 使用 `snake_case`（如 `list_tables`）
- `description` 清晰描述工具功能、适用场景
- 每个 Service 方法职责单一

---

## @McpParam 注解详解

描述工具方法的参数。

```java
@McpParam(
    name = "param_name",       // 参数名（默认取 Java 参数名*）
    description = "参数说明",   // 帮助 Agent 理解参数含义
    required = true            // 是否必填
)
```

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| name | String | 参数名 | Agent 传参时的 key |
| description | String | "" | 参数用途说明 |
| required | boolean | false | 标记必填参数 |

> **注意：** Java 8 编译时默认不保留参数名（ `arg0`, `arg1`），建议显式指定 `name` 或使用 `-parameters` 编译选项。

---

## 参数类型映射

`@McpParam` 的参数 Java 类型自动映射为 JSON Schema 类型：

| Java 类型 | JSON Schema | 说明 |
|-----------|-------------|------|
| `String`, `char` | `"string"` | 字符串 |
| `int`, `long`, `Integer`, `Long` | `"integer"` | 整数 |
| `double`, `float`, `Double`, `Float` | `"number"` | 浮点数 |
| `boolean`, `Boolean` | `"boolean"` | 布尔值 |
| `List`, `Collection`, `数组` | `"array"` | 数组 |
| 其他 | `"object"` | 对象 |

参数值在 `tools/call` 时自动进行类型转换（如 `"123"` → `123`）。

---

## 完整示例：z-agent-mcp-impl-db

参考项目中的 `z-agent-mcp-impl-db` 模块：

```java
@Service
public class McpDbTools {

    private final JdbcTemplate jdbc;

    public McpDbTools(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @McpTool(name = "list_tables", description = "列出数据库中的所有表名")
    public String listTables() {
        List<String> tables = jdbc.queryForList("SHOW TABLES", String.class);
        return "Tables: " + String.join(", ", tables);
    }

    @McpTool(name = "describe_table", description = "查看表的字段结构")
    public String describeTable(
        @McpParam(name = "table", description = "表名", required = true) String table
    ) {
        List<Map<String, Object>> cols = jdbc.queryForList("DESCRIBE " + table);
        // 格式化返回字段信息...
    }

    @McpTool(name = "execute_query", description = "执行 SELECT 查询（只读）")
    public String executeQuery(
        @McpParam(name = "sql", description = "SELECT 语句", required = true) String sql,
        @McpParam(name = "limit", description = "最大返回行数") Integer limit
    ) {
        // 安全检查 + 执行查询...
    }
}
```

**pom.xml：**
```xml
<dependencies>
    <dependency>
        <groupId>com.zifang</groupId>
        <artifactId>z-agent-mcp-starter</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
</dependencies>
```

---

## 自定义端点路径

默认 MCP 端点为 `POST /mcp`。可通过配置修改：

```yaml
# application.yml
mcp:
  endpoint: /api/v2/mcp   # 自定义路径
```

（需要 `McpEndpointController` 中读取此配置，当前版本使用默认路径）

---

## 自动配置原理

引入 starter 后，Spring Boot 自动配置加载流程：

```
spring.factories / AutoConfiguration.imports
  └─► McpAutoConfiguration
        ├─► McpRegistry          (工具注册中心 Bean)
        ├─► McpAnnotationToolExecutor  (反射执行器)
        ├─► McpToolRegistrar     (BeanPostProcessor — 扫描 @McpTool)
        └─► McpEndpointController (POST /mcp 端点)
```

**启动时：**
1. `McpToolRegistrar` 扫描所有 Bean，找到 `@McpTool` 方法
2. 生成 `ToolMeta`（含 inputSchema）注册到 `McpRegistry`
3. 同时注册 `(bean, method)` 映射到 `McpAnnotationToolExecutor`

**tools/call 时：**
1. `McpEndpointController` 收到请求
2. 查询 `McpAnnotationToolExecutor`
3. 反射调用对应方法，传入解析后的参数
4. 返回值封装为 `{content: [{type:"text", text:"..."}], isError: false}`

---

## 模块架构

```
z-agent-mcp-center/
├── z-agent-mcp-core/          # 核心抽象（无 Spring 依赖）
│   ├── ToolMeta.java          #   工具元数据
│   ├── McpRegistry.java       #   工具注册中心
│   └── ToolExecutor.java      #   执行器接口
│
├── z-agent-mcp-starter/       # Spring Boot Starter（本模块）
│   ├── McpTool.java           #   @McpTool 注解
│   ├── McpParam.java          #   @McpParam 注解
│   ├── McpToolRegistrar.java  #   BeanPostProcessor
│   ├── McpAnnotationToolExecutor.java  # 反射执行器
│   ├── McpAutoConfiguration.java  # 自动配置
│   └── McpEndpointController.java  # POST /mcp
│
├── z-agent-mcp-server1/       # 独立 stdio 服务器
└── z-agent-mcp-impl/
    └── z-agent-mcp-impl-db/   # 参考实现：DB 工具
```

---

## 常见问题

### Q: tools/list 有工具，但 tools/call 返回 "Tool not found"

检查 `McpToolRegistrar` 是否注入了 `McpAnnotationToolExecutor`（v1.0 已修复）。

### Q: Java 8 参数名叫 arg0 怎么办？

显式指定 `@McpParam(name = "table")`，或添加编译参数 `-parameters`。

### Q: 如何让 Agent 发现我的 MCP 服务？

在 Hermes Agent 的 `~/.hermes/config.yaml` 中注册：

```yaml
mcp_servers:
  my_db_service:
    url: "http://localhost:8095/mcp"
```

重启 Hermes Agent 后，工具会以 `mcp_my_db_service_list_tables` 等名称自动可用。
