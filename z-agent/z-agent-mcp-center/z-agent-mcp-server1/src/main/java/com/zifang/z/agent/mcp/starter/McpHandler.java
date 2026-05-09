package com.zifang.z.agent.mcp.starter;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.zifang.z.agent.mcp.starter.model.McpError;
import com.zifang.z.agent.mcp.starter.model.McpRequest;
import com.zifang.z.agent.mcp.starter.model.McpResponse;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.*;

/**
 * MCP内置Method处理器
 */
@Component
public class McpHandler {


    @Autowired
    private McpRegistry registry;

    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String MCP_VERSION = "1.0";

    // ===================== 1. initialize：握手协商 =====================
    public McpResponse handleInitialize(McpRequest request) {
        McpResponse response = new McpResponse();
        response.setId(request.getId());

        try {
            Map<String, Object> params = request.getParams();
            // 校验必填参数
            if (params == null || !params.containsKey("protocolVersion")) {
                throw new IllegalArgumentException("缺少必填参数：protocolVersion");
            }

            String clientVersion = params.get("protocolVersion").toString();
            // 协商协议版本（仅支持1.0）
            if (!MCP_VERSION.equals(clientVersion)) {
                throw new IllegalArgumentException("不支持的协议版本：" + clientVersion + "，仅支持" + MCP_VERSION);
            }

            // 构建响应结果
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("protocolVersion", MCP_VERSION);
            result.put("capabilities", Arrays.asList("initialize", "ping", "list_tools", "call_tool", "get_tool_schema", "heartbeat", "shutdown"));
            result.put("message", "MCP握手成功");
            result.put("serverTime", System.currentTimeMillis());

            response.setResult(result);
        } catch (Exception e) {
            McpError error = new McpError();
            error.setCode(-32602);
            error.setMessage(e.getMessage());
            response.setError(error);
        }
        return response;
    }

    // ===================== 2. ping：心跳保活 =====================
    public McpResponse handlePing(McpRequest request) {
        McpResponse response = new McpResponse();
        response.setId(request.getId());

        try {
            Map<String, Object> params = request.getParams();
            long clientTimestamp = params != null && params.containsKey("clientTimestamp")
                    ? Long.parseLong(params.get("clientTimestamp").toString())
                    : 0;

            Map<String, Object> result = new HashMap<>();
            result.put("pong", true);
            result.put("serverTimestamp", System.currentTimeMillis());
            result.put("delay", clientTimestamp > 0 ? System.currentTimeMillis() - clientTimestamp : 0);

            response.setResult(result);
        } catch (Exception e) {
            McpError error = new McpError();
            error.setCode(-32602);
            error.setMessage(e.getMessage());
            response.setError(error);
        }
        return response;
    }

    // ===================== 3. list_tools：查询所有工具 =====================
    public McpResponse handleListTools(McpRequest request) {
        McpResponse response = new McpResponse();
        response.setId(request.getId());

        try {
            Map<String, Object> params = request.getParams();
            String filter = params != null && params.containsKey("filter")
                    ? params.get("filter").toString()
                    : "";

            List<ToolMeta> tools = registry.listTools(filter);
            // 转换为前端友好格式（仅返回核心字段）
            List<Map<String, Object>> toolList = new ArrayList<>();
            for (ToolMeta tool : tools) {
                Map<String, Object> toolInfo = new HashMap<>();
                toolInfo.put("toolName", tool.getToolName());
                toolInfo.put("type", tool.getType());
                toolInfo.put("description", tool.getDescription());
                toolInfo.put("inputSchema", tool.getInputSchema());
                toolList.add(toolInfo);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("tools", toolList);
            result.put("total", toolList.size());
            result.put("filter", filter);

            response.setResult(result);
        } catch (Exception e) {
            McpError error = new McpError();
            error.setCode(-32602);
            error.setMessage(e.getMessage());
            response.setError(error);
        }
        return response;
    }

    // ===================== 4. call_tool：执行指定工具 =====================
    public McpResponse handleCallTool(McpRequest request) {
        McpResponse response = new McpResponse();
        response.setId(request.getId());

        try {
            Map<String, Object> params = request.getParams();
            // 校验必填参数
            if (params == null || !params.containsKey("toolName")) {
                throw new IllegalArgumentException("缺少必填参数：toolName");
            }
            String toolName = params.get("toolName").toString();
            Map<String, Object> toolParams = params.containsKey("parameters")
                    ? (Map<String, Object>) params.get("parameters")
                    : new HashMap<>();

            ToolMeta toolMeta = registry.getToolMeta(toolName);
            if (toolMeta == null) {
                throw new IllegalArgumentException("工具不存在：" + toolName);
            }

            // 内置工具不允许通过call_tool执行
            if (registry.isBuiltInTool(toolName)) {
                throw new IllegalArgumentException("禁止调用内置工具：" + toolName);
            }

            // 转发到第三方服务
            Map<String, Object> thirdPartyResult = callThirdPartyTool(toolMeta, toolParams, request.getId());
            Map<String, Object> result = new HashMap<>();
            result.put("result", thirdPartyResult);
            result.put("toolName", toolName);
            result.put("executionTime", System.currentTimeMillis() - toolMeta.getCreateTime());
            result.put("status", "SUCCESS");

            response.setResult(result);
        } catch (Exception e) {
            McpError error = new McpError();
            error.setCode(-32602);
            error.setMessage(e.getMessage());
            response.setError(error);
        }
        return response;
    }

    // ===================== 5. get_tool_schema：查询工具Schema =====================
    public McpResponse handleGetToolSchema(McpRequest request) {
        McpResponse response = new McpResponse();
        response.setId(request.getId());

        try {
            Map<String, Object> params = request.getParams();
            if (params == null || !params.containsKey("toolName")) {
                throw new IllegalArgumentException("缺少必填参数：toolName");
            }
            String toolName = params.get("toolName").toString();

            ToolMeta toolMeta = registry.getToolMeta(toolName);
            if (toolMeta == null) {
                throw new IllegalArgumentException("工具不存在：" + toolName);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("toolName", toolMeta.getToolName());
            result.put("inputSchema", toolMeta.getInputSchema());
            result.put("outputSchema", toolMeta.getOutputSchema());
            result.put("description", toolMeta.getDescription());

            response.setResult(result);
        } catch (Exception e) {
            McpError error = new McpError();
            error.setCode(-32602);
            error.setMessage(e.getMessage());
            response.setError(error);
        }
        return response;
    }

    // ===================== 6. heartbeat：长连接保活 =====================
    public McpResponse handleHeartbeat(McpRequest request) {
        McpResponse response = new McpResponse();
        response.setId(request.getId());

        try {
            Map<String, Object> params = request.getParams();
            String sessionId = params != null && params.containsKey("sessionId")
                    ? params.get("sessionId").toString()
                    : UUID.randomUUID().toString();

            Map<String, Object> result = new HashMap<>();
            result.put("sessionId", sessionId);
            result.put("active", true);
            result.put("serverTime", System.currentTimeMillis());
            result.put("message", "会话保持成功");

            response.setResult(result);
        } catch (Exception e) {
            McpError error = new McpError();
            error.setCode(-32602);
            error.setMessage(e.getMessage());
            response.setError(error);
        }
        return response;
    }

    // ===================== 7. shutdown：关闭会话 =====================
    public McpResponse handleShutdown(McpRequest request) {
        McpResponse response = new McpResponse();
        response.setId(request.getId());

        try {
            Map<String, Object> params = request.getParams();
            String sessionId = params != null && params.containsKey("sessionId")
                    ? params.get("sessionId").toString()
                    : "";

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("sessionId", sessionId);
            result.put("message", "会话已优雅关闭");
            result.put("shutdownTime", System.currentTimeMillis());

            response.setResult(result);
        } catch (Exception e) {
            McpError error = new McpError();
            error.setCode(-32602);
            error.setMessage(e.getMessage());
            response.setError(error);
        }
        return response;
    }

    // ===================== 私有方法：调用第三方工具 =====================
    private Map<String, Object> callThirdPartyTool(ToolMeta toolMeta, Map<String, Object> params, String requestId) throws IOException {
        // 构建第三方请求
        McpRequest thirdPartyRequest = new McpRequest();
        thirdPartyRequest.setId(requestId);
        thirdPartyRequest.setMethod(toolMeta.getToolName());
        thirdPartyRequest.setParams(params);

        // 构建HTTP请求
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                objectMapper.writeValueAsString(thirdPartyRequest)
        );

        Request request = new Request.Builder()
                .url(toolMeta.getExecuteUrl())
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + toolMeta.getAuthToken())
                .build();

        // 执行请求并解析响应
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("第三方服务返回错误：" + response.code() + " " + response.message());
            }
            return objectMapper.readValue(response.body().string(), Map.class);
        }
    }
}