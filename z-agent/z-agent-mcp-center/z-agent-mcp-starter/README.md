# z-agent-mcp-starter

> Spring Boot Starter：一行注解将 Service 方法暴露为 MCP 工具，供 AI Agent 调用。

## 快速体验

```java
@Service
public class MyService {

    @McpTool(name = "hello", description = "Say hello")
    public String hello(@McpParam(name = "name") String name) {
        return "Hello, " + name + "!";
    }
}
```

引入依赖 → 加注解 → 启动服务 → Agent 即可调用。

## 文档

| 文档 | 说明 |
|------|------|
| [MCP_PROTOCOL.md](_doc/MCP_PROTOCOL.md) | MCP 协议完整规范（JSON-RPC 格式、所有方法、Agent 交互流程） |
| [INTEGRATION.md](_doc/INTEGRATION.md) | 集成指南（依赖配置、注解详解、参数映射、完整示例） |

## 协议方法

| 方法 | 说明 |
|------|------|
| `initialize` | 握手协商（版本 + 能力） |
| `tools/list` | 列出所有可用工具及参数 Schema |
| `tools/call` | 调用指定工具 |
| `resources/list` | 列出资源 |
| `resources/read` | 读取资源内容 |
| `prompts/list` | 列出提示词模板 |
| `prompts/get` | 获取提示词内容 |
| `ping` | 心跳检测 |

## 模块结构

```
z-agent-mcp-starter/
├── McpTool.java              # @McpTool 注解
├── McpParam.java             # @McpParam 注解
├── McpToolRegistrar.java     # 自动扫描注册
├── McpAnnotationToolExecutor.java  # 反射执行
├── McpAutoConfiguration.java # 自动配置
├── McpEndpointController.java     # POST /mcp
├── _doc/
│   ├── MCP_PROTOCOL.md       # 协议规范
│   └── INTEGRATION.md        # 集成指南
└── src/main/resources/META-INF/spring/
    ├── spring.factories
    └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

## 依赖

| 模块 | 说明 |
|------|------|
| `z-agent-mcp-core` | 核心抽象（ToolMeta, McpRegistry, ToolExecutor） |
| `spring-boot-starter-web` | HTTP 端点（provided） |
| `spring-boot-autoconfigure` | 自动配置 |
