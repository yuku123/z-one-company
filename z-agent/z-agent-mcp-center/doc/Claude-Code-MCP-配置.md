# Claude Code MCP 配置指南

本文档介绍如何在 Claude Code 中使用 Z-Agent MCP Server。

## 一、Claude Code 简介

Claude Code 是 Anthropic 推出的命令行 AI 编程助手，支持通过 MCP (Model Context Protocol) 协议与外部工具集成。

## 二、配置步骤

### 2.1 编译 MCP Server

```bash
# 进入项目目录
cd /Users/zifang/workplace/idea_workplace/z-agent/z-agent-mcp-center

# 编译打包
mvn clean package -DskipTests

# 检查生成的 jar 文件
ls -la z-agent-mcp-starter/target/*.jar
```

### 2.2 配置 Claude Code

在 Claude Code 中添加 MCP 服务器配置：

```bash
# 打开 Claude Code 设置
claude config set mcpServers.z-agent-mcp-server '{"command":"java","args":["-jar","/Users/zifang/workplace/idea_workplace/z-agent/z-agent-mcp-center/z-agent-mcp-starter/target/z-agent-mcp-starter-1.0.0.jar","--stdio"]}'
```

或者在配置文件中手动添加（`~/.claude/config.json`）：

```json
{
  "mcpServers": {
    "z-agent-mcp-server": {
      "command": "java",
      "args": [
        "-jar",
        "/Users/zifang/workplace/idea_workplace/z-agent/z-agent-mcp-center/z-agent-mcp-starter/target/z-agent-mcp-starter-1.0.0.jar",
        "--stdio"
      ]
    }
  }
}
```

### 2.3 验证配置

```bash
# 重启 Claude Code
claude

# 在 Claude Code 中查看 MCP 工具
/tools

# 应该能看到 z-agent-mcp-server 提供的工具
```

## 三、可用工具

配置成功后，Claude Code 可以使用以下工具：

### 3.1 内置工具

| 工具名 | 说明 |
|--------|------|
| `initialize` | MCP 协议握手，协商版本和能力集 |
| `ping` | 心跳保活，检测服务端可用性 |
| `list_tools` | 查询所有可用工具 |
| `call_tool` | 执行指定第三方工具 |
| `get_tool_schema` | 查询工具的入参/出参 Schema |
| `heartbeat` | 长连接保活，维持会话 |
| `shutdown` | 优雅关闭会话/连接 |

### 3.2 使用示例

```
# 列出所有工具
/tools

# 调用特定工具
/call_tool toolName=list_tools

# 使用心跳保持连接
/call_tool toolName=heartbeat
```

## 四、故障排查

### 4.1 常见问题

**问题 1: MCP Server 无法启动**

```bash
# 检查 Java 版本
java -version

# 检查 jar 文件是否存在
ls -la /Users/zifang/workplace/idea_workplace/z-agent/z-agent-mcp-center/z-agent-mcp-starter/target/*.jar
```

**问题 2: Claude Code 无法识别 MCP Server**

```bash
# 检查配置
claude config get mcpServers

# 重新加载配置
claude config reload
```

**问题 3: 工具调用失败**

```bash
# 检查日志
claude logs

# 查看详细错误信息
claude --verbose
```

### 4.2 调试模式

启动 MCP Server 时启用调试模式：

```bash
java -jar z-agent-mcp-starter/target/z-agent-mcp-starter-*.jar --stdio --debug
```

### 4.3 日志位置

```
# Claude Code 日志
~/.claude/logs/

# MCP Server 日志（如果配置了文件输出）
/var/log/z-agent-mcp/
```

## 五、高级配置

### 5.1 自定义工具

注册自定义工具：

```java
ToolMeta customTool = new ToolMeta();
customTool.setToolName("my-custom-tool");
customTool.setType("THIRD_PARTY");
customTool.setDescription("My custom tool description");
customTool.setExecuteUrl("http://localhost:8080/api/my-tool");
customTool.setInputSchema(...);
registry.registerTool(customTool);
```

### 5.2 安全配置

启用认证：

```json
{
  "mcpServers": {
    "z-agent-mcp-server": {
      "command": "java",
      "args": [
        "-jar",
        "z-agent-mcp-starter.jar",
        "--stdio",
        "--auth-token=your-secret-token"
      ]
    }
  }
}
```

## 六、参考资源

- [MCP 官方文档](https://modelcontextprotocol.io/)
- [Claude Code 文档](https://docs.anthropic.com/en/docs/agents-and-tools/claude-code/overview)
- [MCP 协议规范](https://spec.modelcontextprotocol.io/)

---

*最后更新: 2026-04-01*
