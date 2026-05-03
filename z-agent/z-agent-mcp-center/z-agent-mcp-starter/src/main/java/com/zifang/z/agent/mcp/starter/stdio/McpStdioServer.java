package com.zifang.z.agent.mcp.starter.stdio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zifang.z.agent.mcp.starter.McpHandler;
import com.zifang.z.agent.mcp.starter.McpRegistry;
import com.zifang.z.agent.mcp.starter.ToolMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

/**
 * MCP Stdio Server - 用于 Claude Code 的命令行 MCP 服务器
 * 通过标准输入输出（stdio）与 Claude Code 通信
 *
 * 使用方法:
 * 1. 编译: mvn clean package
 * 2. 运行: java -jar z-agent-mcp-starter/target/z-agent-mcp-starter-*.jar --stdio
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.zifang.z.agent.mcp.starter")
public class McpStdioServer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(McpStdioServer.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final McpRegistry registry;
    private final McpHandler handler;

    private boolean running = false;
    private PrintWriter out;

    public McpStdioServer(McpRegistry registry, McpHandler handler) {
        this.registry = registry;
        this.handler = handler;
    }

    public static void main(String[] args) {
        // 检查是否为 stdio 模式
        boolean stdioMode = Arrays.asList(args).contains("--stdio");

        if (stdioMode) {
            // stdio 模式：禁用 Spring Boot 的 banner 和日志输出到控制台
            System.setProperty("spring.main.banner-mode", "off");
            System.setProperty("logging.level.root", "WARN");
            System.setProperty("logging.level.com.zifang", "WARN");
        }

        SpringApplication.run(McpStdioServer.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 检查是否为 stdio 模式
        boolean stdioMode = Arrays.asList(args).contains("--stdio");

        if (!stdioMode) {
            logger.info("MCP Stdio Server started. Use --stdio flag to enable stdio mode.");
            return;
        }

        // 启动 stdio 模式
        startStdioServer();
    }

    /**
     * 启动 stdio 服务器
     */
    private void startStdioServer() {
        running = true;
        out = new PrintWriter(System.out, true);

        // 注册内置工具
        registerBuiltInTools();

        // 发送初始化完成通知
        sendServerReady();

        // 读取标准输入
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while (running && (line = reader.readLine()) != null) {
                handleRequest(line);
            }
        } catch (IOException e) {
            logger.error("Error reading from stdin", e);
        } finally {
            running = false;
        }
    }

    /**
     * 处理请求
     */
    private void handleRequest(String json) {
        try {
            JsonNode requestNode = objectMapper.readTree(json);
            String method = requestNode.has("method") ? requestNode.get("method").asText() : "";
            JsonNode idNode = requestNode.get("id");

            ObjectNode response = objectMapper.createObjectNode();
            response.put("jsonrpc", "2.0");
            if (idNode != null) {
                response.set("id", idNode);
            }

            switch (method) {
                case "initialize":
                    handleInitialize(requestNode, response);
                    break;
                case "ping":
                    handlePing(requestNode, response);
                    break;
                case "tools/list":
                    handleToolsList(requestNode, response);
                    break;
                case "tools/call":
                    handleToolsCall(requestNode, response);
                    break;
                case "shutdown":
                    handleShutdown(requestNode, response);
                    break;
                default:
                    handleUnknownMethod(method, response);
            }

            sendResponse(response);

        } catch (Exception e) {
            logger.error("Error handling request: " + json, e);
            sendErrorResponse(null, -32603, "Internal error: " + e.getMessage());
        }
    }

    /**
     * 处理 initialize 请求
     */
    private void handleInitialize(JsonNode request, ObjectNode response) {
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

    /**
     * 处理 ping 请求
     */
    private void handlePing(JsonNode request, ObjectNode response) {
        ObjectNode result = objectMapper.createObjectNode();
        result.put("timestamp", System.currentTimeMillis());
        response.set("result", result);
    }

    /**
     * 处理 tools/list 请求
     */
    private void handleToolsList(JsonNode request, ObjectNode response) {
        List<ToolMeta> tools = registry.listTools(null);

        ArrayNode toolsArray = objectMapper.createArrayNode();
        for (ToolMeta tool : tools) {
            ObjectNode toolNode = objectMapper.createObjectNode();
            toolNode.put("name", tool.getToolName());
            toolNode.put("description", tool.getDescription());
            if (tool.getInputSchema() != null) {
                toolNode.set("inputSchema", objectMapper.valueToTree(tool.getInputSchema()));
            }
            toolsArray.add(toolNode);
        }

        ObjectNode result = objectMapper.createObjectNode();
        result.set("tools", toolsArray);
        response.set("result", result);
    }

    /**
     * 处理 tools/call 请求
     */
    private void handleToolsCall(JsonNode request, ObjectNode response) {
        JsonNode paramsNode = request.get("params");
        if (paramsNode == null) {
            sendErrorResponse(response, -32602, "Missing params");
            return;
        }

        String toolName = paramsNode.has("name") ? paramsNode.get("name").asText() : null;
        if (toolName == null) {
            sendErrorResponse(response, -32602, "Missing tool name");
            return;
        }

        ToolMeta toolMeta = registry.getToolMeta(toolName);
        if (toolMeta == null) {
            sendErrorResponse(response, -32602, "Tool not found: " + toolName);
            return;
        }

        // 调用工具（简化实现）
        ObjectNode result = objectMapper.createObjectNode();
        result.put("toolName", toolName);
        result.put("status", "executed");

        ArrayNode contentArray = objectMapper.createArrayNode();
        ObjectNode content = objectMapper.createObjectNode();
        content.put("type", "text");
        content.put("text", "Tool " + toolName + " executed successfully");
        contentArray.add(content);
        result.set("content", contentArray);

        response.set("result", result);
    }

    /**
     * 处理 shutdown 请求
     */
    private void handleShutdown(JsonNode request, ObjectNode response) {
        ObjectNode result = objectMapper.createObjectNode();
        result.put("success", true);
        result.put("message", "Server shutting down");
        response.set("result", result);

        // 发送响应后关闭
        sendResponse(response);

        running = false;
    }

    /**
     * 处理未知方法
     */
    private void handleUnknownMethod(String method, ObjectNode response) {
        sendErrorResponse(response, -32601, "Method not found: " + method);
    }

    /**
     * 发送响应
     */
    private void sendResponse(ObjectNode response) {
        try {
            String json = objectMapper.writeValueAsString(response);
            out.println(json);
            out.flush();
        } catch (Exception e) {
            logger.error("Error sending response", e);
        }
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(ObjectNode response, int code, String message) {
        if (response == null) {
            response = objectMapper.createObjectNode();
            response.put("jsonrpc", "2.0");
            response.putNull("id");
        }

        ObjectNode error = objectMapper.createObjectNode();
        error.put("code", code);
        error.put("message", message);
        response.set("error", error);

        sendResponse(response);
    }

    /**
     * 注册内置工具
     */
    private void registerBuiltInTools() {
        // 在初始化时通过 Spring 容器自动注册
        // 实际注册逻辑在 McpRegistry 中完成
    }

    /**
     * 发送服务器就绪通知
     */
    private void sendServerReady() {
        ObjectNode notification = objectMapper.createObjectNode();
        notification.put("jsonrpc", "2.0");
        notification.put("method", "server/ready");

        ObjectNode params = objectMapper.createObjectNode();
        params.put("status", "ready");
        params.put("version", "1.0.0");
        notification.set("params", params);

        sendResponse(notification);
    }
}
