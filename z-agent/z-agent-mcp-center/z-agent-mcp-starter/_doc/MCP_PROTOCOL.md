# MCP 协议规范 (Model Context Protocol)

> 基于 MCP 2024-11-05 规范，本文档描述 z-agent-mcp-starter 实现的完整协议。

---

## 1. 协议概述

MCP（Model Context Protocol）是 AI Agent 与外部服务之间的标准通信协议，基于 **JSON-RPC 2.0**。

**核心思想：** 服务提供方通过 `tools/list` 声明自己能做什么，Agent 通过 `tools/call` 调用这些能力。双方通过 `initialize` 握手协商版本和能力集。

```
┌──────────┐  JSON-RPC 2.0   ┌──────────────┐
│  Agent   │ ◄──────────────► │  MCP Server  │
│ (Client) │   over HTTP/stdio│  (Service)   │
└──────────┘                  └──────────────┘
```

---

## 2. 传输层

### 2.1 HTTP POST（推荐）

- 端点：`POST /mcp`
- Content-Type：`application/json`
- 每次请求一次响应，无会话保持

### 2.2 stdio（命令行 Agent）

- 进程间通信：stdin 接收 JSON-RPC，stdout 返回响应
- 每行一个完整的 JSON-RPC 消息
- 长连接：进程启动后保持 open

---

## 3. JSON-RPC 2.0 格式

### 3.1 请求格式

```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "method": "tools/list",
  "params": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| jsonrpc | string | 固定 "2.0" |
| id | number/string | 请求标识，响应原样返回 |
| method | string | 方法名（如 `tools/list`） |
| params | object | 方法参数 |

### 3.2 成功响应

```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "result": { ... }
}
```

### 3.3 错误响应

```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "error": {
    "code": -32601,
    "message": "Method not found: xxx"
  }
}
```

**标准错误码：**

| Code | 含义 |
|------|------|
| -32700 | Parse error（JSON 解析失败） |
| -32600 | Invalid Request |
| -32601 | Method not found |
| -32602 | Invalid params |
| -32603 | Internal error |

---

## 4. 协议方法

### 4.1 initialize — 握手协商

Agent 连接后第一条消息，协商协议版本和能力集。

**请求：**
```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "method": "initialize",
  "params": {
    "protocolVersion": "2024-11-05",
    "capabilities": {},
    "clientInfo": {
      "name": "hermes-agent",
      "version": "1.0.0"
    }
  }
}
```

**响应：**
```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "result": {
    "protocolVersion": "2024-11-05",
    "serverInfo": {
      "name": "z-agent-mcp-starter",
      "version": "1.0.0"
    },
    "capabilities": {
      "tools": { "listChanged": false },
      "resources": { "subscribe": false, "listChanged": false },
      "prompts": { "listChanged": false }
    }
  }
}
```

**capabilities 说明：**

| 能力 | 含义 |
|------|------|
| `tools` | 支持工具调用 |
| `tools.listChanged` | 工具列表是否会动态变化（变化时发送通知） |
| `resources` | 支持资源读取 |
| `resources.subscribe` | 支持资源订阅（变更推送） |
| `prompts` | 支持提示词模板 |

---

### 4.2 tools/list — 列出可用工具

Agent 发现服务端提供的所有工具及其参数 Schema。

**请求：**
```json
{
  "jsonrpc": "2.0",
  "id": 2,
  "method": "tools/list",
  "params": {}
}
```

**响应：**
```json
{
  "jsonrpc": "2.0",
  "id": 2,
  "result": {
    "tools": [
      {
        "name": "list_tables",
        "description": "列出数据库中的所有表名",
        "inputSchema": {
          "type": "object",
          "properties": {},
          "required": []
        }
      },
      {
        "name": "describe_table",
        "description": "查看表的字段结构",
        "inputSchema": {
          "type": "object",
          "properties": {
            "table": {
              "type": "string",
              "description": "表名"
            }
          },
          "required": ["table"]
        }
      }
    ],
    "nextCursor": null
  }
}
```

**inputSchema 格式：** 标准 [JSON Schema](https://json-schema.org/) 格式。

---

### 4.3 tools/call — 调用工具

Agent 执行具体工具。**这是最核心的方法。**

**请求：**
```json
{
  "jsonrpc": "2.0",
  "id": 3,
  "method": "tools/call",
  "params": {
    "name": "describe_table",
    "arguments": {
      "table": "system_params"
    }
  }
}
```

| 参数 | 类型 | 说明 |
|------|------|------|
| name | string | 工具名（来自 tools/list） |
| arguments | object | 工具参数（key-value） |

**成功响应：**
```json
{
  "jsonrpc": "2.0",
  "id": 3,
  "result": {
    "content": [
      {
        "type": "text",
        "text": "=== system_params (7 columns) ===\nid  varchar(255)  NO  PRI\n..."
      }
    ],
    "isError": false
  }
}
```

**错误响应（工具执行失败）：**
```json
{
  "jsonrpc": "2.0",
  "id": 3,
  "result": {
    "content": [
      {
        "type": "text",
        "text": "Error: Table 'xxx' does not exist"
      }
    ],
    "isError": true
  }
}
```

> **注意：** 工具执行失败时，HTTP 状态码仍为 200，通过 `isError: true` 标识。JSON-RPC 层错误（如 method not found）才返回 `error` 字段。

**content 格式：**

| 类型 | 说明 |
|------|------|
| `{"type":"text","text":"..."}` | 纯文本（最常用） |
| `{"type":"image","data":"base64...","mimeType":"image/png"}` | 图片（支持） |
| `{"type":"resource","resource":{"uri":"...","mimeType":"..."}}` | 资源引用（支持） |

---

### 4.4 resources/list — 列出资源

服务端声明可读取的资源（文件、状态信息等）。

**请求：**
```json
{ "jsonrpc": "2.0", "id": 4, "method": "resources/list", "params": {} }
```

**响应：**
```json
{
  "result": {
    "resources": [
      {
        "uri": "project://modules",
        "name": "Project Modules",
        "description": "Module list",
        "mimeType": "text/plain"
      }
    ]
  }
}
```

### 4.5 resources/read — 读取资源

```json
{ "jsonrpc": "2.0", "id": 5, "method": "resources/read", "params": { "uri": "project://status" } }
```

### 4.6 prompts/list — 列出提示词模板

服务端提供的可复用 prompt 模板。

```json
{
  "result": {
    "prompts": [
      {
        "name": "code_review",
        "description": "Code review prompt template",
        "arguments": [
          { "name": "file_path", "description": "File path", "required": true }
        ]
      }
    ]
  }
}
```

### 4.7 prompts/get — 获取提示词

```json
{ "jsonrpc": "2.0", "id": 7, "method": "prompts/get", "params": { "name": "code_review", "arguments": { "file_path": "/src/Main.java" } } }
```

### 4.8 ping — 心跳

```json
{ "jsonrpc": "2.0", "id": 8, "method": "ping", "params": {} }
```

---

## 5. Agent 交互流程

### 5.1 完整的 Agent 对话生命周期

```
 Agent                          MCP Server
   │                                │
   │──── initialize ───────────────►│  ① 握手：协商版本和能力
   │◄─── capabilities + serverInfo ─│
   │                                │
   │──── tools/list ───────────────►│  ② 发现：获取工具列表
   │◄─── [tool1, tool2, ...] ──────│
   │                                │
   │──── tools/call ───────────────►│  ③ 调用：执行工具
   │◄─── {content, isError} ───────│
   │                                │
   │──── tools/call ───────────────►│  ④ 再次调用
   │◄─── {content, isError} ───────│
   │                                │
```

### 5.2 Agent 如何使用工具

1. **发现阶段**：Agent 调用 `initialize` + `tools/list`，获取工具名、描述、参数 Schema
2. **规划阶段**：根据用户意图，Agent 选择合适的工具和参数
3. **执行阶段**：调用 `tools/call`，解析返回的 `content` 文本
4. **响应阶段**：将工具结果整合到对用户的回复中

### 5.3 示例：Agent 处理用户请求 "列出数据库表"

```
用户: "列出数据库有哪些表"

Agent 内部:
  1. tools/list  → 发现有 "list_tables" 工具
  2. tools/call {name:"list_tables", arguments:{}}  
     → 返回 30 张表的列表
  3. 回复用户: "数据库有 30 张表: orders, system_params, ..."
```

---

## 6. 与 Hermes Agent 集成

### 6.1 配置方式

在 `~/.hermes/config.yaml` 中注册 MCP 服务：

```yaml
# HTTP 模式（推荐）
mcp_servers:
  db:
    url: "http://localhost:8095/mcp"
    timeout: 60

# stdio 模式
mcp_servers:
  server1:
    command: "java"
    args: ["-jar", "z-agent-mcp-server1.jar", "--stdio"]
    timeout: 60
    connect_timeout: 30
```

### 6.2 Agent 重启后

Hermes Agent 重启时自动：
1. 连接所有配置的 MCP 服务器
2. 调用 `initialize` 握手
3. 调用 `tools/list` 发现工具
4. 注册为 `mcp_{server_name}_{tool_name}` 格式的工具

例如：`mcp_db_list_tables`, `mcp_db_describe_table`

### 6.3 Agent 调用流程

当 Hermes Agent 调用 `mcp_db_list_tables` 时，内部执行：
1. 向 `http://localhost:8095/mcp` 发送 `tools/call`
2. 解析返回的 `content[0].text`
3. 将文本结果注入当前对话上下文
