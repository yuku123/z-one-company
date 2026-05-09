package com.zifang.z.agent.mcp.starter.service;

import com.zifang.z.agent.mcp.starter.McpHandler;
import com.zifang.z.agent.mcp.starter.McpRegistry;
import com.zifang.z.agent.mcp.starter.ToolMeta;
import com.zifang.z.agent.mcp.starter.ToolExecutor;
import com.zifang.z.agent.mcp.starter.BuiltInToolExecutor;
import com.zifang.z.agent.mcp.starter.protocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * MCP 协议服务 - 处理所有标准 MCP 请求
 */
@Service
public class McpProtocolService {

    private static final Logger logger = LoggerFactory.getLogger(McpProtocolService.class);

    @Autowired
    private McpRegistry mcpRegistry;

    @Autowired
    private McpHandler mcpHandler;

    @Autowired
    private BuiltInToolExecutor builtInToolExecutor;

    /**
     * 处理 MCP 请求
     */
    public McpResponseV1 handleRequest(McpRequestV1 request, McpSession session) {
        if (request == null || request.getMethod() == null) {
            return McpResponseV1.error(null,
                    McpProtocolConstants.ERROR_INVALID_REQUEST,
                    "Invalid request: method is required");
        }

        String method = request.getMethod();
        logger.debug("Handling MCP request: method={}, sessionId={}", method, session.getSessionId());

        try {
            switch (method) {
                // 初始化
                case McpProtocolConstants.METHOD_INITIALIZE:
                    return handleInitialize(request, session);

                // 心跳
                case McpProtocolConstants.METHOD_PING:
                    return handlePing(request, session);

                // 工具相关
                case McpProtocolConstants.METHOD_TOOLS_LIST:
                    return handleToolsList(request, session);

                case McpProtocolConstants.METHOD_TOOLS_CALL:
                    return handleToolsCall(request, session);

                // 资源相关
                case McpProtocolConstants.METHOD_RESOURCES_LIST:
                    return handleResourcesList(request, session);

                case McpProtocolConstants.METHOD_RESOURCES_READ:
                    return handleResourcesRead(request, session);

                // 提示词相关
                case McpProtocolConstants.METHOD_PROMPTS_LIST:
                    return handlePromptsList(request, session);

                case McpProtocolConstants.METHOD_PROMPTS_GET:
                    return handlePromptsGet(request, session);

                // 默认处理
                default:
                    return McpResponseV1.error(request.getId(),
                            McpProtocolConstants.ERROR_METHOD_NOT_FOUND,
                            "Method not found: " + method);
            }
        } catch (Exception e) {
            logger.error("Error handling MCP request: method={}", method, e);
            return McpResponseV1.error(request.getId(),
                    McpProtocolConstants.ERROR_INTERNAL_ERROR,
                    "Internal error: " + e.getMessage());
        }
    }

    // ==================== 初始化 ====================

    private McpResponseV1 handleInitialize(McpRequestV1 request, McpSession session) {
        Map<String, Object> params = request.getParams();
        if (params == null) {
            return McpResponseV1.error(request.getId(),
                    McpProtocolConstants.ERROR_INVALID_PARAMS,
                    "Missing params");
        }

        // 提取客户端能力
        Object clientCaps = params.get("capabilities");
        if (clientCaps instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> caps = (Map<String, Object>) clientCaps;
            // 保存客户端能力到会话
        }

        // 协商协议版本
        String protocolVersion = (String) params.get("protocolVersion");
        if (protocolVersion == null) {
            protocolVersion = McpProtocolConstants.PROTOCOL_VERSION;
        }

        // 更新会话
        session.setProtocolVersion(protocolVersion);
        session.setStatus(McpProtocolConstants.SESSION_STATUS_ACTIVE);

        // 构建响应
        Map<String, Object> result = new HashMap<>();
        result.put("protocolVersion", McpProtocolConstants.PROTOCOL_VERSION);
        result.put("serverInfo", createServerInfo());
        result.put("capabilities", createCapabilities());

        return McpResponseV1.success(request.getId(), result);
    }

    // ==================== 心跳 ====================

    private McpResponseV1 handlePing(McpRequestV1 request, McpSession session) {
        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", System.currentTimeMillis());
        return McpResponseV1.success(request.getId(), result);
    }

    // ==================== 工具 ====================

    private McpResponseV1 handleToolsList(McpRequestV1 request, McpSession session) {
        Map<String, Object> params = request.getParams();
        String filter = null;
        if (params != null && params.containsKey("filter")) {
            filter = (String) params.get("filter");
        }

        // 获取工具列表
        List<ToolMeta> tools = mcpRegistry.listTools(filter);

        // 转换为 MCP 标准格式
        List<Map<String, Object>> toolList = new ArrayList<>();
        for (ToolMeta tool : tools) {
            Map<String, Object> toolInfo = new HashMap<>();
            toolInfo.put("name", tool.getToolName());
            toolInfo.put("description", tool.getDescription());
            toolInfo.put("inputSchema", tool.getInputSchema());
            if (tool.getOutputSchema() != null) {
                toolInfo.put("outputSchema", tool.getOutputSchema());
            }
            toolList.add(toolInfo);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("tools", toolList);
        result.put("nextCursor", null); // 分页支持

        return McpResponseV1.success(request.getId(), result);
    }

    private McpResponseV1 handleToolsCall(McpRequestV1 request, McpSession session) {
        Map<String, Object> params = request.getParams();
        if (params == null) {
            return McpResponseV1.error(request.getId(),
                    McpProtocolConstants.ERROR_INVALID_PARAMS,
                    "Missing params");
        }

        String toolName = (String) params.get("name");
        if (toolName == null) {
            return McpResponseV1.error(request.getId(),
                    McpProtocolConstants.ERROR_INVALID_PARAMS,
                    "Missing tool name");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> arguments = (Map<String, Object>) params.get("arguments");
        if (arguments == null) {
            arguments = new HashMap<>();
        }

        // 获取工具元数据
        ToolMeta toolMeta = mcpRegistry.getToolMeta(toolName);
        if (toolMeta == null) {
            return McpResponseV1.error(request.getId(),
                    McpProtocolConstants.ERROR_INVALID_PARAMS,
                    "Tool not found: " + toolName);
        }

        try {
            Map<String, Object> result;

            // 内置工具：本地执行
            if (mcpRegistry.isBuiltInTool(toolName)) {
                result = builtInToolExecutor.execute(toolName, arguments);
            } else {
                // 第三方工具：转发到远程服务
                result = callThirdPartyTool(toolMeta, arguments, request.getId());
            }

            return McpResponseV1.success(request.getId(), result);
        } catch (Exception e) {
            logger.error("Error calling tool: {}", toolName, e);
            // 返回 MCP 标准错误格式
            Map<String, Object> errorResult = new HashMap<>();
            List<Map<String, Object>> content = new ArrayList<>();
            Map<String, Object> item = new HashMap<>();
            item.put("type", "text");
            item.put("text", "Error calling tool '" + toolName + "': " + e.getMessage());
            content.add(item);
            errorResult.put("content", content);
            errorResult.put("isError", true);
            return McpResponseV1.success(request.getId(), errorResult);
        }
    }

    // ==================== 资源 ====================

    private McpResponseV1 handleResourcesList(McpRequestV1 request, McpSession session) {
        // TODO: 实现资源列表
        Map<String, Object> result = new HashMap<>();
        result.put("resources", new ArrayList<>());
        result.put("nextCursor", null);
        return McpResponseV1.success(request.getId(), result);
    }

    private McpResponseV1 handleResourcesRead(McpRequestV1 request, McpSession session) {
        // TODO: 实现资源读取
        return McpResponseV1.error(request.getId(),
                McpProtocolConstants.ERROR_METHOD_NOT_FOUND,
                "Not implemented");
    }

    // ==================== 提示词 ====================

    private McpResponseV1 handlePromptsList(McpRequestV1 request, McpSession session) {
        // TODO: 实现提示词列表
        Map<String, Object> result = new HashMap<>();
        result.put("prompts", new ArrayList<>());
        result.put("nextCursor", null);
        return McpResponseV1.success(request.getId(), result);
    }

    private McpResponseV1 handlePromptsGet(McpRequestV1 request, McpSession session) {
        // TODO: 实现提示词获取
        return McpResponseV1.error(request.getId(),
                McpProtocolConstants.ERROR_METHOD_NOT_FOUND,
                "Not implemented");
    }

    // ==================== 私有辅助方法 ====================

    private Map<String, Object> callThirdPartyTool(ToolMeta toolMeta, Map<String, Object> arguments, Object requestId)
            throws IOException {
        // 这里需要调用实际的第三方服务
        // 简化实现：返回模拟数据
        Map<String, Object> result = new HashMap<>();
        result.put("content", new ArrayList<Map<String, Object>>());
        result.put("isError", false);
        return result;
    }

    private Map<String, Object> createServerInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "z-agent-mcp-server");
        info.put("version", "1.0.0");
        return info;
    }

    private Map<String, Object> createCapabilities() {
        Map<String, Object> caps = new HashMap<>();

        Map<String, Object> tools = new HashMap<>();
        tools.put("listChanged", true);
        caps.put("tools", tools);

        Map<String, Object> resources = new HashMap<>();
        resources.put("subscribe", true);
        resources.put("listChanged", true);
        caps.put("resources", resources);

        Map<String, Object> prompts = new HashMap<>();
        prompts.put("listChanged", true);
        caps.put("prompts", prompts);

        caps.put("logging", true);

        return caps;
    }
}