package com.zifang.z.agent.mcp.service1;

import com.zifang.z.agent.mcp.core.McpRegistry;
import com.zifang.z.agent.mcp.core.ToolMeta;
import com.zifang.z.agent.mcp.service1.model.McpError;
import com.zifang.z.agent.mcp.service1.model.McpRequest;
import com.zifang.z.agent.mcp.service1.model.McpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

/**
 * MCP核心控制器（Java 8 兼容版）
 * 实现MCP协议7个内置Method，支持第三方工具注册/注销
 */
@RestController
@RequestMapping("/mcp")
public class McpServerController {

    @Autowired
    private McpRegistry registry;

    @Autowired
    private McpHandler handler;

    // ===================== 初始化：注册7个内置Method对应的工具（Java 8 兼容） =====================
    @PostConstruct
    public void initBuiltInTools() {
        // 1. initialize（内置工具）
        ToolMeta initializeTool = new ToolMeta();
        initializeTool.setToolName("initialize");
        initializeTool.setType("BUILT_IN");
        initializeTool.setDescription("MCP协议握手，协商版本和能力集");

        // Java 8 兼容：替换 Map.of() 为 HashMap
        Map<String, Object> initializeInputSchema = new HashMap<>();
        initializeInputSchema.put("type", "object");
        Map<String, Object> initializeInputProps = new HashMap<>();

        Map<String, Object> protocolVersionProp = new HashMap<>();
        protocolVersionProp.put("type", "string");
        protocolVersionProp.put("description", "MCP协议版本");
        initializeInputProps.put("protocolVersion", protocolVersionProp);

        Map<String, Object> capabilitiesProp = new HashMap<>();
        capabilitiesProp.put("type", "array");
        capabilitiesProp.put("description", "客户端支持的能力集");
        initializeInputProps.put("capabilities", capabilitiesProp);

        Map<String, Object> authProp = new HashMap<>();
        authProp.put("type", "object");
        authProp.put("description", "认证信息");
        initializeInputProps.put("auth", authProp);

        initializeInputSchema.put("properties", initializeInputProps);
        Map<String, Boolean> initializeRequired = new HashMap<>();
        initializeRequired.put("protocolVersion", true);
        initializeInputSchema.put("required", initializeRequired);
        initializeTool.setInputSchema(initializeInputSchema);

        // Output Schema
        Map<String, Object> initializeOutputSchema = new HashMap<>();
        initializeOutputSchema.put("type", "object");
        Map<String, Object> initializeOutputProps = new HashMap<>();

        Map<String, Object> successProp = new HashMap<>();
        successProp.put("type", "boolean");
        initializeOutputProps.put("success", successProp);

        Map<String, Object> versionProp = new HashMap<>();
        versionProp.put("type", "string");
        initializeOutputProps.put("protocolVersion", versionProp);

        Map<String, Object> capsProp = new HashMap<>();
        capsProp.put("type", "array");
        initializeOutputProps.put("capabilities", capsProp);
        initializeOutputSchema.put("properties", initializeOutputProps);
        initializeTool.setOutputSchema(initializeOutputSchema);
        registry.registerTool(initializeTool);

        // 2. ping（内置工具）
        ToolMeta pingTool = new ToolMeta();
        pingTool.setToolName("ping");
        pingTool.setType("BUILT_IN");
        pingTool.setDescription("心跳保活，检测服务端可用性");

        Map<String, Object> pingInputSchema = new HashMap<>();
        pingInputSchema.put("type", "object");
        Map<String, Object> pingInputProps = new HashMap<>();

        Map<String, Object> clientTsProp = new HashMap<>();
        clientTsProp.put("type", "number");
        clientTsProp.put("description", "客户端时间戳");
        pingInputProps.put("clientTimestamp", clientTsProp);
        pingInputSchema.put("properties", pingInputProps);
        pingTool.setInputSchema(pingInputSchema);

        Map<String, Object> pingOutputSchema = new HashMap<>();
        pingOutputSchema.put("type", "object");
        Map<String, Object> pingOutputProps = new HashMap<>();

        Map<String, Object> pongProp = new HashMap<>();
        pongProp.put("type", "boolean");
        pingOutputProps.put("pong", pongProp);

        Map<String, Object> serverTsProp = new HashMap<>();
        serverTsProp.put("type", "number");
        pingOutputProps.put("serverTimestamp", serverTsProp);

        Map<String, Object> delayProp = new HashMap<>();
        delayProp.put("type", "number");
        pingOutputProps.put("delay", delayProp);
        pingOutputSchema.put("properties", pingOutputProps);
        pingTool.setOutputSchema(pingOutputSchema);
        registry.registerTool(pingTool);

        // 3. list_tools（内置工具）
        ToolMeta listToolsTool = new ToolMeta();
        listToolsTool.setToolName("list_tools");
        listToolsTool.setType("BUILT_IN");
        listToolsTool.setDescription("查询所有可用工具");

        Map<String, Object> listToolsInputSchema = new HashMap<>();
        listToolsInputSchema.put("type", "object");
        Map<String, Object> listToolsInputProps = new HashMap<>();

        Map<String, Object> filterProp = new HashMap<>();
        filterProp.put("type", "string");
        filterProp.put("description", "按类型过滤：BUILT_IN/THIRD_PARTY");
        listToolsInputProps.put("filter", filterProp);
        listToolsInputSchema.put("properties", listToolsInputProps);
        listToolsTool.setInputSchema(listToolsInputSchema);

        Map<String, Object> listToolsOutputSchema = new HashMap<>();
        listToolsOutputSchema.put("type", "object");
        Map<String, Object> listToolsOutputProps = new HashMap<>();

        Map<String, Object> toolsProp = new HashMap<>();
        toolsProp.put("type", "array");
        listToolsOutputProps.put("tools", toolsProp);

        Map<String, Object> totalProp = new HashMap<>();
        totalProp.put("type", "number");
        listToolsOutputProps.put("total", totalProp);

        Map<String, Object> filterOutProp = new HashMap<>();
        filterOutProp.put("type", "string");
        listToolsOutputProps.put("filter", filterOutProp);
        listToolsOutputSchema.put("properties", listToolsOutputProps);
        listToolsTool.setOutputSchema(listToolsOutputSchema);
        registry.registerTool(listToolsTool);

        // 4. call_tool（内置工具）
        ToolMeta callToolTool = new ToolMeta();
        callToolTool.setToolName("call_tool");
        callToolTool.setType("BUILT_IN");
        callToolTool.setDescription("执行指定第三方工具");

        Map<String, Object> callToolInputSchema = new HashMap<>();
        callToolInputSchema.put("type", "object");
        Map<String, Object> callToolInputProps = new HashMap<>();

        Map<String, Object> toolNameProp = new HashMap<>();
        toolNameProp.put("type", "string");
        toolNameProp.put("description", "工具名称");
        callToolInputProps.put("toolName", toolNameProp);

        Map<String, Object> paramsProp = new HashMap<>();
        paramsProp.put("type", "object");
        paramsProp.put("description", "工具参数");
        callToolInputProps.put("parameters", paramsProp);

        Map<String, Object> timeoutProp = new HashMap<>();
        timeoutProp.put("type", "number");
        timeoutProp.put("description", "超时时间（ms）");
        callToolInputProps.put("timeout", timeoutProp);
        callToolInputSchema.put("properties", callToolInputProps);

        Map<String, Boolean> callToolRequired = new HashMap<>();
        callToolRequired.put("toolName", true);
        callToolInputSchema.put("required", callToolRequired);
        callToolTool.setInputSchema(callToolInputSchema);

        Map<String, Object> callToolOutputSchema = new HashMap<>();
        callToolOutputSchema.put("type", "object");
        Map<String, Object> callToolOutputProps = new HashMap<>();

        Map<String, Object> resultProp = new HashMap<>();
        resultProp.put("type", "object");
        callToolOutputProps.put("result", resultProp);

        Map<String, Object> toolNameOutProp = new HashMap<>();
        toolNameOutProp.put("type", "string");
        callToolOutputProps.put("toolName", toolNameOutProp);

        Map<String, Object> statusProp = new HashMap<>();
        statusProp.put("type", "string");
        callToolOutputProps.put("status", statusProp);
        callToolOutputSchema.put("properties", callToolOutputProps);
        callToolTool.setOutputSchema(callToolOutputSchema);
        registry.registerTool(callToolTool);

        // 5. get_tool_schema（内置工具）
        ToolMeta getToolSchemaTool = new ToolMeta();
        getToolSchemaTool.setToolName("get_tool_schema");
        getToolSchemaTool.setType("BUILT_IN");
        getToolSchemaTool.setDescription("查询工具的入参/出参Schema");

        Map<String, Object> getSchemaInputSchema = new HashMap<>();
        getSchemaInputSchema.put("type", "object");
        Map<String, Object> getSchemaInputProps = new HashMap<>();

        Map<String, Object> schemaToolNameProp = new HashMap<>();
        schemaToolNameProp.put("type", "string");
        schemaToolNameProp.put("description", "工具名称");
        getSchemaInputProps.put("toolName", schemaToolNameProp);
        getSchemaInputSchema.put("properties", getSchemaInputProps);

        Map<String, Boolean> getSchemaRequired = new HashMap<>();
        getSchemaRequired.put("toolName", true);
        getSchemaInputSchema.put("required", getSchemaRequired);
        getToolSchemaTool.setInputSchema(getSchemaInputSchema);

        Map<String, Object> getSchemaOutputSchema = new HashMap<>();
        getSchemaOutputSchema.put("type", "object");
        Map<String, Object> getSchemaOutputProps = new HashMap<>();

        Map<String, Object> schemaToolNameOutProp = new HashMap<>();
        schemaToolNameOutProp.put("type", "string");
        getSchemaOutputProps.put("toolName", schemaToolNameOutProp);

        Map<String, Object> inputSchemaProp = new HashMap<>();
        inputSchemaProp.put("type", "object");
        getSchemaOutputProps.put("inputSchema", inputSchemaProp);

        Map<String, Object> outputSchemaProp = new HashMap<>();
        outputSchemaProp.put("type", "object");
        getSchemaOutputProps.put("outputSchema", outputSchemaProp);

        Map<String, Object> descProp = new HashMap<>();
        descProp.put("type", "string");
        getSchemaOutputProps.put("description", descProp);
        getSchemaOutputSchema.put("properties", getSchemaOutputProps);
        getToolSchemaTool.setOutputSchema(getSchemaOutputSchema);
        registry.registerTool(getToolSchemaTool);

        // 6. heartbeat（内置工具）
        ToolMeta heartbeatTool = new ToolMeta();
        heartbeatTool.setToolName("heartbeat");
        heartbeatTool.setType("BUILT_IN");
        heartbeatTool.setDescription("长连接保活，维持会话");

        Map<String, Object> heartbeatInputSchema = new HashMap<>();
        heartbeatInputSchema.put("type", "object");
        Map<String, Object> heartbeatInputProps = new HashMap<>();

        Map<String, Object> sessionIdProp = new HashMap<>();
        sessionIdProp.put("type", "string");
        sessionIdProp.put("description", "会话ID");
        heartbeatInputProps.put("sessionId", sessionIdProp);
        heartbeatInputSchema.put("properties", heartbeatInputProps);
        heartbeatTool.setInputSchema(heartbeatInputSchema);

        Map<String, Object> heartbeatOutputSchema = new HashMap<>();
        heartbeatOutputSchema.put("type", "object");
        Map<String, Object> heartbeatOutputProps = new HashMap<>();

        Map<String, Object> sessionIdOutProp = new HashMap<>();
        sessionIdOutProp.put("type", "string");
        heartbeatOutputProps.put("sessionId", sessionIdOutProp);

        Map<String, Object> activeProp = new HashMap<>();
        activeProp.put("type", "boolean");
        heartbeatOutputProps.put("active", activeProp);

        Map<String, Object> serverTimeProp = new HashMap<>();
        serverTimeProp.put("type", "number");
        heartbeatOutputProps.put("serverTime", serverTimeProp);

        Map<String, Object> msgProp = new HashMap<>();
        msgProp.put("type", "string");
        heartbeatOutputProps.put("message", msgProp);
        heartbeatOutputSchema.put("properties", heartbeatOutputProps);
        heartbeatTool.setOutputSchema(heartbeatOutputSchema);
        registry.registerTool(heartbeatTool);

        // 7. shutdown（内置工具）
        ToolMeta shutdownTool = new ToolMeta();
        shutdownTool.setToolName("shutdown");
        shutdownTool.setType("BUILT_IN");
        shutdownTool.setDescription("优雅关闭会话/连接");

        Map<String, Object> shutdownInputSchema = new HashMap<>();
        shutdownInputSchema.put("type", "object");
        Map<String, Object> shutdownInputProps = new HashMap<>();

        Map<String, Object> shutdownSessionIdProp = new HashMap<>();
        shutdownSessionIdProp.put("type", "string");
        shutdownSessionIdProp.put("description", "会话ID");
        shutdownInputProps.put("sessionId", shutdownSessionIdProp);
        shutdownInputSchema.put("properties", shutdownInputProps);
        shutdownTool.setInputSchema(shutdownInputSchema);

        Map<String, Object> shutdownOutputSchema = new HashMap<>();
        shutdownOutputSchema.put("type", "object");
        Map<String, Object> shutdownOutputProps = new HashMap<>();

        Map<String, Object> shutdownSuccessProp = new HashMap<>();
        shutdownSuccessProp.put("type", "boolean");
        shutdownOutputProps.put("success", shutdownSuccessProp);

        Map<String, Object> shutdownSessionIdOutProp = new HashMap<>();
        shutdownSessionIdOutProp.put("type", "string");
        shutdownOutputProps.put("sessionId", shutdownSessionIdOutProp);

        Map<String, Object> shutdownTimeProp = new HashMap<>();
        shutdownTimeProp.put("type", "number");
        shutdownOutputProps.put("shutdownTime", shutdownTimeProp);
        shutdownOutputSchema.put("properties", shutdownOutputProps);
        shutdownTool.setOutputSchema(shutdownOutputSchema);
        registry.registerTool(shutdownTool);
    }

    // ===================== 1. MCP唯一入口：处理所有Method调用 =====================
    @PostMapping
    public McpResponse handleMcpRequest(@RequestBody McpRequest request) {
        McpResponse response = new McpResponse();
        response.setId(request.getId());

        // 校验基础参数
        if (request.getMethod() == null || request.getMethod().isEmpty()) {
            McpError error = new McpError();
            error.setCode(-32600);
            error.setMessage("无效的请求：Method名称不能为空");
            response.setError(error);
            return response;
        }

        // 路由到对应处理器（Java 8 switch 兼容）
        String method = request.getMethod();
        switch (method) {
            case "initialize":
                return handler.handleInitialize(request);
            case "ping":
                return handler.handlePing(request);
            case "list_tools":
                return handler.handleListTools(request);
            case "call_tool":
                return handler.handleCallTool(request);
            case "get_tool_schema":
                return handler.handleGetToolSchema(request);
            case "heartbeat":
                return handler.handleHeartbeat(request);
            case "shutdown":
                return handler.handleShutdown(request);
            default:
                // 非内置Method：检查是否为第三方工具
                ToolMeta toolMeta = registry.getToolMeta(request.getMethod());
                if (toolMeta == null || registry.isBuiltInTool(request.getMethod())) {
                    McpError error = new McpError();
                    error.setCode(-32601);
                    error.setMessage("未找到对应的Method：" + request.getMethod());
                    response.setError(error);
                    return response;
                }
                // 直接调用第三方工具（等价于call_tool）
                return handler.handleCallTool(wrapToCallToolRequest(request));
        }
    }

    // ===================== 2. 第三方工具注册接口 =====================
    @PostMapping("/registry")
    public Map<String, Object> registerTool(@RequestBody ToolRegisterRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            ToolMeta toolMeta = new ToolMeta();
            toolMeta.setToolName(request.getToolName());
            toolMeta.setType("THIRD_PARTY");
            toolMeta.setDescription(request.getDescription());
            toolMeta.setInputSchema(request.getInputSchema());
            toolMeta.setOutputSchema(request.getOutputSchema());
            toolMeta.setExecuteUrl(request.getExecuteUrl());
            toolMeta.setAuthToken(request.getAuthToken());

            boolean success = registry.registerTool(toolMeta);
            if (success) {
                result.put("success", true);
                result.put("message", "工具注册成功：" + request.getToolName());
                result.put("toolName", request.getToolName());
            } else {
                result.put("success", false);
                result.put("message", "工具已存在：" + request.getToolName());
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "注册失败：" + e.getMessage());
        }
        return result;
    }

    // ===================== 3. 第三方工具注销接口 =====================
    @DeleteMapping("/registry/{toolName}")
    public Map<String, Object> unregisterTool(@PathVariable String toolName) {
        Map<String, Object> result = new HashMap<>();
        boolean success = registry.unregisterTool(toolName);
        if (success) {
            result.put("success", true);
            result.put("message", "工具注销成功：" + toolName);
        } else {
            result.put("success", false);
            result.put("message", "工具不存在或为内置工具：" + toolName);
        }
        return result;
    }

    // ===================== 私有方法：包装为call_tool请求（Java 8 兼容） =====================
    private McpRequest wrapToCallToolRequest(McpRequest originalRequest) {
        McpRequest callToolRequest = new McpRequest();
        callToolRequest.setId(originalRequest.getId());
        callToolRequest.setMethod("call_tool");

        Map<String, Object> params = new HashMap<>();
        params.put("toolName", originalRequest.getMethod());
        params.put("parameters", originalRequest.getParams() == null ? new HashMap<>() : originalRequest.getParams());
        callToolRequest.setParams(params);

        return callToolRequest;
    }
}