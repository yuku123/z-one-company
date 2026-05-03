# MCP 传输协议完整实现

本文档提供 MCP (Model Context Protocol) 三种传输协议的完整格式说明、消息流程和实现代码：

1. **SSE (Server-Sent Events)** - 服务器推送事件
2. **Streamable HTTP** - 可流式传输的 HTTP
3. **WebSocket** - 全双工通信

---

## 一、SSE (Server-Sent Events)

### 1.1 协议概述

SSE 是一种服务器向客户端推送实时更新的技术，基于 HTTP 协议，使用 `text/event-stream` MIME 类型。

**特点：**
- 单向通信：服务器 → 客户端
- 基于 HTTP，易于穿透防火墙
- 自动重连机制
- 轻量级，适合服务器推送场景

### 1.2 协议格式

#### 基础格式

```
Content-Type: text/event-stream
Cache-Control: no-cache
Connection: keep-alive

data: {"jsonrpc":"2.0","id":"1","result":{...}}

event: message
data: {"jsonrpc":"2.0","method":"notification",...}

id: 123
data: {"jsonrpc":"2.0",...}

retry: 5000
data: {"jsonrpc":"2.0",...}
```

#### 字段说明

| 字段 | 说明 | 示例 |
|------|------|------|
| `data` | 消息数据（必须） | `data: {"key":"value"}` |
| `event` | 事件类型（可选） | `event: message` |
| `id` | 事件 ID（可选） | `id: 123` |
| `retry` | 重连间隔毫秒（可选） | `retry: 5000` |

### 1.3 MCP SSE 协议流程

#### 连接建立

```http
GET /mcp/sse HTTP/1.1
Host: localhost:8080
Accept: text/event-stream
Cache-Control: no-cache
Mcp-Session-Id: <optional-session-id>

HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache
Connection: keep-alive
Mcp-Session-Id: <generated-session-id>

event: endpoint
data: /mcp/message?sessionId=<session-id>

event: sessionId
data: <session-id>
```

#### 发送消息（客户端 → 服务器）

```http
POST /mcp/message?sessionId=<session-id> HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Mcp-Session-Id: <session-id>

{
  "jsonrpc": "2.0",
  "id": "1",
  "method": "tools/list",
  "params": {}
}

HTTP/1.1 202 Accepted
Content-Length: 0
```

#### 接收消息（服务器 → 客户端，通过 SSE）

```
event: message
data: {"jsonrpc":"2.0","id":"1","result":{"tools":[...]}}
```

### 1.4 完整示例代码

#### Java Spring Boot 实现

```java
@RestController
@RequestMapping("/mcp")
public class McpSseController {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * SSE 连接端点
     */
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connectSse(
            @RequestParam(required = false) String sessionId,
            @RequestHeader(value = "Mcp-Session-Id", required = false) String headerSessionId) {

        String finalSessionId = headerSessionId != null ? headerSessionId :
                (sessionId != null ? sessionId : UUID.randomUUID().toString());

        // 创建 SSE Emitter（无超时）
        SseEmitter emitter = new SseEmitter(0L);
        emitters.put(finalSessionId, emitter);

        // 发送初始事件
        try {
            // 发送 endpoint 事件
            emitter.send(SseEmitter.event()
                    .name("endpoint")
                    .data("/mcp/message?sessionId=" + finalSessionId));

            // 发送 sessionId 事件
            emitter.send(SseEmitter.event()
                    .name("sessionId")
                    .data(finalSessionId));
        } catch (IOException e) {
            logger.error("Error sending initial SSE events", e);
        }

        // 清理 on completion
        emitter.onCompletion(() -> emitters.remove(finalSessionId));
        emitter.onTimeout(() -> emitters.remove(finalSessionId));
        emitter.onError((e) -> emitters.remove(finalSessionId));

        return emitter;
    }

    /**
     * 消息接收端点
     */
    @PostMapping(value = "/message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> receiveMessage(
            @RequestBody JsonNode request,
            @RequestParam String sessionId,
            @RequestHeader(value = "Mcp-Session-Id", required = false) String headerSessionId) {

        String finalSessionId = headerSessionId != null ? headerSessionId : sessionId;

        // 处理请求
        JsonNode response = processRequest(request, finalSessionId);

        // 通过 SSE 发送响应
        SseEmitter emitter = emitters.get(finalSessionId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(objectMapper.writeValueAsString(response)));
            } catch (IOException e) {
                logger.error("Error sending SSE message", e);
            }
        }

        return ResponseEntity.accepted().build();
    }

    /**
     * 向指定会话推送消息
     */
    public void pushMessage(String sessionId, Object message) {
        SseEmitter emitter = emitters.get(sessionId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(objectMapper.writeValueAsString(message)));
            } catch (IOException e) {
                logger.error("Error pushing message", e);
            }
        }
    }

    private JsonNode processRequest(JsonNode request, String sessionId) {
        // 实现请求处理逻辑
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.set("id", request.get("id"));

        // 根据 method 处理...
        String method = request.has("method") ? request.get("method").asText() : "";

        switch (method) {
            case "tools/list":
                response.set("result", objectMapper.createObjectNode().set("tools", objectMapper.createArrayNode()));
                break;
            default:
                ObjectNode error = objectMapper.createObjectNode();
                error.put("code", -32601);
                error.put("message", "Method not found: " + method);
                response.set("error", error);
        }

        return response;
    }
}
```

---

## 二、Streamable HTTP

### 2.1 协议概述

Streamable HTTP 是一种基于 HTTP 的流式传输协议，允许服务器在单个 HTTP 连接上发送多个响应。

**特点：**
- 基于标准 HTTP/1.1 或 HTTP/2
- 支持 chunked transfer encoding
- 双向流式通信
- 更好的防火墙穿透性

### 2.2 协议格式

#### 请求格式

```http
POST /mcp/stream HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Transfer-Encoding: chunked
Accept: application/x-ndjson

{
  "jsonrpc": "2.0",
  "id": "1",
  "method": "tools/list",
  "params": {}
}
```

#### 响应格式（NDJSON - Newline Delimited JSON）

```http
HTTP/1.1 200 OK
Content-Type: application/x-ndjson
Transfer-Encoding: chunked

{"jsonrpc":"2.0","id":"1","result":{"tools":[]}}
{"jsonrpc":"2.0","method":"notifications/progress","params":{"token":"1","progress":50}}
{"jsonrpc":"2.0","id":"2","result":{"content":[{"type":"text","text":"Result"}]}}
```

### 2.3 完整示例代码

#### Java Spring Boot 实现

```java
@RestController
@RequestMapping("/mcp")
public class McpStreamableHttpController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Streamable HTTP 端点
     */
    @PostMapping(value = "/stream", produces = "application/x-ndjson")
    public ResponseEntity<StreamingResponseBody> streamMcp(
            @RequestBody JsonNode request) throws IOException {

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("application/x-ndjson"))
                .body(outputStream -> {
                    try (PrintWriter writer = new PrintWriter(outputStream)) {
                        // 处理请求并流式返回结果
                        processStreamRequest(request, writer);
                    }
                });
    }

    /**
     * Chunked HTTP 端点（使用 Transfer-Encoding: chunked）
     */
    @PostMapping(value = "/chunked", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<String> chunkedMcp(@RequestBody JsonNode request) {
        return Flux.create(sink -> {
            try {
                String requestId = request.has("id") ? request.get("id").asText() : UUID.randomUUID().toString();
                String method = request.has("method") ? request.get("method").asText() : "";

                // 根据方法处理
                switch (method) {
                    case "tools/list":
                        ObjectNode result = objectMapper.createObjectNode();
                        result.set("tools", objectMapper.createArrayNode());

                        ObjectNode response = objectMapper.createObjectNode();
                        response.put("jsonrpc", "2.0");
                        response.put("id", requestId);
                        response.set("result", result);

                        sink.next(response.toString());
                        break;

                    case "tools/call":
                        // 模拟流式响应
                        for (int i = 0; i <= 100; i += 25) {
                            ObjectNode progress = objectMapper.createObjectNode();
                            progress.put("jsonrpc", "2.0");
                            progress.put("method", "notifications/progress");

                            ObjectNode params = objectMapper.createObjectNode();
                            params.put("token", requestId);
                            params.put("progress", i);
                            params.put("total", 100);
                            progress.set("params", params);

                            sink.next(progress.toString());
                        }

                        // 最终结果
                        ObjectNode finalResult = objectMapper.createObjectNode();
                        finalResult.put("jsonrpc", "2.0");
                        finalResult.put("id", requestId);

                        ObjectNode content = objectMapper.createObjectNode();
                        ArrayNode contentArray = objectMapper.createArrayNode();
                        ObjectNode textContent = objectMapper.createObjectNode();
                        textContent.put("type", "text");
                        textContent.put("text", "Tool execution completed");
                        contentArray.add(textContent);
                        content.set("content", contentArray);
                        content.put("isError", false);

                        finalResult.set("result", content);
                        sink.next(finalResult.toString());
                        break;

                    default:
                        ObjectNode error = objectMapper.createObjectNode();
                        error.put("jsonrpc", "2.0");
                        error.put("id", requestId);

                        ObjectNode errorObj = objectMapper.createObjectNode();
                        errorObj.put("code", -32601);
                        errorObj.put("message", "Method not found: " + method);
                        error.set("error", errorObj);

                        sink.next(error.toString());
                }

                sink.complete();
            } catch (Exception e) {
                sink.error(e);
            }
        });
    }

    private void processStreamRequest(JsonNode request, PrintWriter writer) throws IOException {
        String requestId = request.has("id") ? request.get("id").asText() : UUID.randomUUID().toString();
        String method = request.has("method") ? request.get("method").asText() : "";

        // 发送 NDJSON 格式的响应
        switch (method) {
            case "tools/list":
                ObjectNode listResponse = objectMapper.createObjectNode();
                listResponse.put("jsonrpc", "2.0");
                listResponse.put("id", requestId);
                ObjectNode listResult = objectMapper.createObjectNode();
                listResult.set("tools", objectMapper.createArrayNode());
                listResponse.set("result", listResult);

                writer.println(listResponse.toString());
                break;

            case "tools/call":
                // 发送进度通知
                for (int i = 0; i <= 100; i += 25) {
                    ObjectNode progress = objectMapper.createObjectNode();
                    progress.put("jsonrpc", "2.0");
                    progress.put("method", "notifications/progress");

                    ObjectNode params = objectMapper.createObjectNode();
                    params.put("token", requestId);
                    params.put("progress", i);
                    params.put("total", 100);
                    progress.set("params", params);

                    writer.println(progress.toString());
                    writer.flush(); // 确保立即发送
                }

                // 发送最终结果
                ObjectNode finalResponse = objectMapper.createObjectNode();
                finalResponse.put("jsonrpc", "2.0");
                finalResponse.put("id", requestId);

                ObjectNode finalResult = objectMapper.createObjectNode();
                ArrayNode contentArray = objectMapper.createArrayNode();
                ObjectNode content = objectMapper.createObjectNode();
                content.put("type", "text");
                content.put("text", "Tool execution completed successfully");
                contentArray.add(content);
                finalResult.set("content", contentArray);
                finalResult.put("isError", false);
                finalResponse.set("result", finalResult);

                writer.println(finalResponse.toString());
                break;

            default:
                ObjectNode errorResponse = objectMapper.createObjectNode();
                errorResponse.put("jsonrpc", "2.0");
                errorResponse.put("id", requestId);

                ObjectNode error = objectMapper.createObjectNode();
                error.put("code", -32601);
                error.put("message", "Method not found: " + method);
                errorResponse.set("error", error);

                writer.println(errorResponse.toString());
        }

        writer.flush();
    }
}
```

---

## 三、WebSocket

### 3.1 协议概述

WebSocket 是一种在单个 TCP 连接上进行全双工通信的协议。

**特点：**
- 全双工通信：客户端和服务器可以同时发送和接收消息
- 基于 TCP，性能高
- 持久连接，无需重复建立连接
- 适合实时双向通信场景

### 3.2 协议格式

#### WebSocket 握手

**请求：**
```http
GET /mcp/ws HTTP/1.1
Host: localhost:8080
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==
Sec-WebSocket-Version: 13
```

**响应：**
```http
HTTP/1.1 101 Switching Protocols
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Accept: s3pPLMBiTxaQ9kYGzzhZRbK+xOo=
```

#### WebSocket 消息格式

**文本帧（JSON-RPC）：**
```json
{
  "jsonrpc": "2.0",
  "id": "1",
  "method": "tools/list",
  "params": {}
}
```

**二进制帧（可选，用于大文件传输）：**
```
[Binary data - e.g., file content, images]
```

### 3.3 MCP WebSocket 协议流程

#### 连接建立

```
Client                                          Server
  │                                               │
  │── GET /mcp/ws ─────────────────────────────>│
  │   Upgrade: websocket                          │
  │   Connection: Upgrade                         │
  │                                               │
  │<── HTTP/1.1 101 Switching Protocols ─────────│
  │   Upgrade: websocket                          │
  │   Connection: Upgrade                         │
  │                                               │
  │<── WebSocket: Connected ──────────────────────│
```

#### 消息交换

```
Client                                          Server
  │                                               │
  │── WebSocket: Text Frame ────────────────────>│
  │   {
  │     "jsonrpc": "2.0",
  │     "id": "1",
  │     "method": "tools/list"
  │   }                                           │
  │                                               │
  │<── WebSocket: Text Frame ─────────────────────│
  │   {
  │     "jsonrpc": "2.0",
  │     "id": "1",
  │     "result": {
  │       "tools": [...]
  │     }
  │   }                                           │
  │                                               │
  │<── WebSocket: Text Frame ─────────────────────│
  │   {
  │     "jsonrpc": "2.0",
  │     "method": "notifications/progress",
  │     "params": {
  │       "token": "1",
  │       "progress": 50
  │     }
  │   }                                           │
```

#### 连接关闭

```
Client                                          Server
  │                                               │
  │── WebSocket: Close Frame ───────────────────>│
  │   Code: 1000 (Normal Closure)                 │
  │                                               │
  │<── WebSocket: Close Frame ────────────────────│
  │   Code: 1000 (Normal Closure)                 │
  │                                               │
  │<── TCP: Connection Closed ───────────────────│
```

### 3.4 完整示例代码

#### Java Spring Boot + WebSocket 实现

**WebSocket 配置：**
```java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(mcpWebSocketHandler(), "/mcp/ws")
                .setAllowedOrigins("*");
    }

    @Bean
    public McpWebSocketHandler mcpWebSocketHandler() {
        return new McpWebSocketHandler();
    }
}
```

**WebSocket Handler 实现：**
```java
@Component
public class McpWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(McpWebSocketHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        sessions.put(sessionId, session);

        logger.info("WebSocket connection established: sessionId={}", sessionId);

        // 发送连接确认
        ObjectNode confirmation = objectMapper.createObjectNode();
        confirmation.put("jsonrpc", "2.0");
        confirmation.put("method", "connection/established");

        ObjectNode params = objectMapper.createObjectNode();
        params.put("sessionId", sessionId);
        confirmation.set("params", params);

        session.sendMessage(new TextMessage(confirmation.toString()));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String sessionId = session.getId();
        String payload = message.getPayload();

        logger.debug("Received WebSocket message: sessionId={}, payload={}", sessionId, payload);

        try {
            JsonNode request = objectMapper.readTree(payload);
            JsonNode response = processRequest(request, sessionId);

            if (response != null) {
                session.sendMessage(new TextMessage(response.toString()));
            }
        } catch (Exception e) {
            logger.error("Error processing WebSocket message", e);

            // 发送错误响应
            ObjectNode errorResponse = objectMapper.createObjectNode();
            errorResponse.put("jsonrpc", "2.0");
            errorResponse.putNull("id");

            ObjectNode error = objectMapper.createObjectNode();
            error.put("code", -32603);
            error.put("message", "Internal error: " + e.getMessage());
            errorResponse.set("error", error);

            session.sendMessage(new TextMessage(errorResponse.toString()));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        sessions.remove(sessionId);

        logger.info("WebSocket connection closed: sessionId={}, status={}", sessionId, status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("WebSocket transport error: sessionId={}", session.getId(), exception);
    }

    /**
     * 处理请求
     */
    private JsonNode processRequest(JsonNode request, String sessionId) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");

        JsonNode idNode = request.get("id");
        if (idNode != null) {
            response.set("id", idNode);
        }

        String method = request.has("method") ? request.get("method").asText() : "";
        JsonNode params = request.get("params");

        switch (method) {
            case "initialize":
                handleInitialize(params, response);
                break;

            case "ping":
                handlePing(params, response);
                break;

            case "tools/list":
                handleToolsList(params, response);
                break;

            case "tools/call":
                handleToolsCall(params, response);
                break;

            case "resources/list":
                handleResourcesList(params, response);
                break;

            case "prompts/list":
                handlePromptsList(params, response);
                break;

            default:
                ObjectNode error = objectMapper.createObjectNode();
                error.put("code", -32601);
                error.put("message", "Method not found: " + method);
                response.set("error", error);
        }

        return response;
    }

    private void handleInitialize(JsonNode params, ObjectNode response) {
        ObjectNode result = objectMapper.createObjectNode();
        result.put("protocolVersion", "2024-11-05");

        ObjectNode serverInfo = objectMapper.createObjectNode();
        serverInfo.put("name", "z-agent-mcp-server");
        serverInfo.put("version", "1.0.0");
        result.set("serverInfo", serverInfo);

        ObjectNode capabilities = objectMapper.createObjectNode();
        ObjectNode tools = objectMapper.createObjectNode();
        tools.put("listChanged", true);
        capabilities.set("tools", tools);
        result.set("capabilities", capabilities);

        response.set("result", result);
    }

    private void handlePing(JsonNode params, ObjectNode response) {
        ObjectNode result = objectMapper.createObjectNode();
        result.put("timestamp", System.currentTimeMillis());
        response.set("result", result);
    }

    private void handleToolsList(JsonNode params, ObjectNode response) {
        ObjectNode result = objectMapper.createObjectNode();
        result.set("tools", objectMapper.createArrayNode());
        response.set("result", result);
    }

    private void handleToolsCall(JsonNode params, ObjectNode response) {
        ObjectNode result = objectMapper.createObjectNode();

        ArrayNode content = objectMapper.createArrayNode();
        ObjectNode textContent = objectMapper.createObjectNode();
        textContent.put("type", "text");
        textContent.put("text", "Tool executed successfully");
        content.add(textContent);

        result.set("content", content);
        result.put("isError", false);

        response.set("result", result);
    }

    private void handleResourcesList(JsonNode params, ObjectNode response) {
        ObjectNode result = objectMapper.createObjectNode();
        result.set("resources", objectMapper.createArrayNode());
        response.set("result", result);
    }

    private void handlePromptsList(JsonNode params, ObjectNode response) {
        ObjectNode result = objectMapper.createObjectNode();
        result.set("prompts", objectMapper.createArrayNode());
        response.set("result", result);
    }

    /**
     * 向指定会话广播消息
     */
    public void broadcastMessage(Object message) {
        String jsonMessage;
        try {
            jsonMessage = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            logger.error("Error serializing message", e);
            return;
        }

        for (WebSocketSession session : sessions.values()) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(jsonMessage));
                } catch (IOException e) {
                    logger.error("Error broadcasting message", e);
                }
            }
        }
    }
}
```

#### 前端 JavaScript 客户端示例

```javascript
// WebSocket 客户端
class McpWebSocketClient {
    constructor(url) {
        this.url = url;
        this.ws = null;
        this.messageHandlers = new Map();
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
    }

    connect() {
        this.ws = new WebSocket(this.url);

        this.ws.onopen = () => {
            console.log('WebSocket connected');
            this.reconnectAttempts = 0;

            // 发送 initialize 请求
            this.send({
                jsonrpc: '2.0',
                id: '1',
                method: 'initialize',
                params: {
                    protocolVersion: '2024-11-05',
                    capabilities: {},
                    clientInfo: {
                        name: 'mcp-client',
                        version: '1.0.0'
                    }
                }
            });
        };

        this.ws.onmessage = (event) => {
            const message = JSON.parse(event.data);
            this.handleMessage(message);
        };

        this.ws.onclose = () => {
            console.log('WebSocket disconnected');
            this.attemptReconnect();
        };

        this.ws.onerror = (error) => {
            console.error('WebSocket error:', error);
        };
    }

    send(message) {
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
            this.ws.send(JSON.stringify(message));
        } else {
            console.error('WebSocket is not connected');
        }
    }

    handleMessage(message) {
        console.log('Received message:', message);

        // 处理服务器推送的消息
        if (message.method === 'notifications/progress') {
            console.log('Progress:', message.params.progress + '%');
        }

        // 调用注册的消息处理器
        const handler = this.messageHandlers.get(message.id);
        if (handler) {
            handler(message);
            this.messageHandlers.delete(message.id);
        }
    }

    onMessage(id, handler) {
        this.messageHandlers.set(id, handler);
    }

    attemptReconnect() {
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.reconnectAttempts++;
            console.log(`Attempting to reconnect... (${this.reconnectAttempts}/${this.maxReconnectAttempts})`);
            setTimeout(() => this.connect(), 3000 * this.reconnectAttempts);
        } else {
            console.error('Max reconnect attempts reached');
        }
    }

    close() {
        if (this.ws) {
            this.ws.close();
        }
    }
}

// 使用示例
const client = new McpWebSocketClient('ws://localhost:8080/mcp/ws');

client.onMessage('1', (response) => {
    console.log('Initialize response:', response);

    // 获取工具列表
    client.send({
        jsonrpc: '2.0',
        id: '2',
        method: 'tools/list',
        params: {}
    });
});

client.onMessage('2', (response) => {
    console.log('Tools list:', response);
});

client.connect();
```

---

## 三、三种传输协议对比

| 特性 | SSE | Streamable HTTP | WebSocket |
|------|-----|-----------------|-----------|
| **通信方式** | 服务器 → 客户端（单向） | 双向 | 双向（全双工） |
| **协议基础** | HTTP/1.1 | HTTP/1.1 或 HTTP/2 | TCP |
| **连接类型** | 长连接 | 可长可短 | 持久连接 |
| **数据格式** | text/event-stream | application/x-ndjson | Binary/Text |
| **自动重连** | 原生支持 | 需手动实现 | 需手动实现 |
| **防火墙穿透** | 良好 | 良好 | 一般 |
| **浏览器支持** | 原生支持 | 原生支持 | 原生支持 |
| **适用场景** | 服务器推送 | 流式请求/响应 | 实时双向通信 |

---

## 四、选择建议

### 4.1 选择 SSE 的场景

- 主要需要服务器向客户端推送数据
- 需要简单的实现和良好的浏览器支持
- 不需要客户端向服务器频繁发送数据
- 需要通过防火墙或代理服务器

### 4.2 选择 Streamable HTTP 的场景

- 需要流式请求和响应
- 需要处理大量数据
- 需要与现有的 HTTP 基础设施集成
- 需要简单的负载均衡和缓存

### 4.3 选择 WebSocket 的场景

- 需要真正的双向实时通信
- 需要低延迟的消息传递
- 需要频繁的双向数据交换
- 不需要频繁地创建和关闭连接

---

*文档结束*
