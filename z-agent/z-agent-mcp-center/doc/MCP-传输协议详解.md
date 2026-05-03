# MCP 传输协议详解

本文档详细介绍 MCP (Model Context Protocol) 支持的三种传输协议：

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
                ObjectNode textContent = objectMapper.createObjectNode();
                textContent.put("type", "text");
                textContent.put("text", "Tool execution completed successfully");
                contentArray.add(textContent);
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
