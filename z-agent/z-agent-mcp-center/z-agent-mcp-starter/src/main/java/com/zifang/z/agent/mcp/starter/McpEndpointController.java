package com.zifang.z.agent.mcp.starter;

import com.zifang.z.agent.mcp.core.McpRegistry;
import com.zifang.z.agent.mcp.core.ToolMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * MCP JSON-RPC HTTP 端点。
 *
 * 暴露 POST /mcp 作为 MCP 协议入口，
 * 支持 initialize, tools/list, tools/call, resources/list, resources/read, prompts/list, prompts/get, ping
 */
@RestController
@RequestMapping("/mcp")
public class McpEndpointController {

    private static final Logger log = LoggerFactory.getLogger(McpEndpointController.class);

    private final McpRegistry registry;
    private final McpAnnotationToolExecutor executor;

    public McpEndpointController(McpRegistry registry, McpAnnotationToolExecutor executor) {
        this.registry = registry;
        this.executor = executor;
    }

    /**
     * JSON-RPC 2.0 统一入口
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> handleRpc(@RequestBody Map<String, Object> request) {
        String method = (String) request.getOrDefault("method", "");
        Object id = request.get("id");

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("jsonrpc", "2.0");
        if (id != null) response.put("id", id);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> params = (Map<String, Object>) request.getOrDefault("params", Collections.emptyMap());

            Object result = null;

            switch (method) {
                case "initialize":
                    result = buildInitResult();
                    break;
                case "ping":
                    result = Collections.emptyMap();
                    break;
                case "tools/list":
                    result = buildToolsList();
                    break;
                case "tools/call":
                    result = handleToolCall(params);
                    break;
                case "resources/list":
                    result = buildResourcesList();
                    break;
                case "resources/read":
                    result = handleResourceRead(params);
                    break;
                case "prompts/list":
                    result = buildPromptsList();
                    break;
                case "prompts/get":
                    result = handlePromptGet(params);
                    break;
                default:
                    response.put("error", errorObj(-32601, "Method not found: " + method));
                    return ResponseEntity.ok(response);
            }

            response.put("result", result);

        } catch (Exception e) {
            log.error("Error handling MCP request: {}", method, e);
            response.put("error", errorObj(-32603, e.getMessage()));
        }

        return ResponseEntity.ok(response);
    }

    // ── initialize ──
    private Map<String, Object> buildInitResult() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("protocolVersion", "2024-11-05");

        Map<String, Object> serverInfo = new LinkedHashMap<>();
        serverInfo.put("name", "z-agent-mcp-starter");
        serverInfo.put("version", "1.0.0");
        result.put("serverInfo", serverInfo);

        Map<String, Object> caps = new LinkedHashMap<>();
        caps.put("tools", mapOf("listChanged", false));
        caps.put("resources", mapOf("subscribe", false, "listChanged", false));
        caps.put("prompts", mapOf("listChanged", false));
        result.put("capabilities", caps);

        return result;
    }

    // ── tools/list ──
    private Map<String, Object> buildToolsList() {
        List<ToolMeta> tools = registry.listTools(null);
        List<Map<String, Object>> toolList = new ArrayList<>();
        for (ToolMeta t : tools) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", t.getToolName());
            item.put("description", t.getDescription());
            if (t.getInputSchema() != null) {
                item.put("inputSchema", t.getInputSchema());
            }
            toolList.add(item);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("tools", toolList);
        result.put("nextCursor", null);
        return result;
    }

    // ── tools/call ──
    private Map<String, Object> handleToolCall(Map<String, Object> params) {
        String toolName = (String) params.get("name");
        if (toolName == null) throw new IllegalArgumentException("Missing tool name");

        @SuppressWarnings("unchecked")
        Map<String, Object> arguments = (Map<String, Object>) params.getOrDefault("arguments", Collections.emptyMap());

        return executor.execute(toolName, arguments);
    }

    // ── resources ──
    private Map<String, Object> buildResourcesList() {
        List<Map<String, Object>> resources = new ArrayList<>();
        resources.add(resourceItem("project://modules", "Project Modules", "Module list"));
        resources.add(resourceItem("project://status", "Server Status", "Status info"));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("resources", resources);
        result.put("nextCursor", null);
        return result;
    }

    private Map<String, Object> handleResourceRead(Map<String, Object> params) {
        String uri = (String) params.get("uri");
        Map<String, Object> content = new LinkedHashMap<>();
        content.put("uri", uri);
        content.put("mimeType", "text/plain");
        content.put("text", "Resource: " + uri + " (registered tools: " + registry.size() + ")");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("contents", Collections.singletonList(content));
        return result;
    }

    // ── prompts ──
    private Map<String, Object> buildPromptsList() {
        List<Map<String, Object>> prompts = new ArrayList<>();
        prompts.add(promptItem("code_review", "Code review template",
                argItem("file_path", "File path", true)));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("prompts", prompts);
        result.put("nextCursor", null);
        return result;
    }

    private Map<String, Object> handlePromptGet(Map<String, Object> params) {
        String name = (String) params.get("name");
        Map<String, Object> msg = new LinkedHashMap<>();
        msg.put("role", "user");
        Map<String, Object> content = new LinkedHashMap<>();
        content.put("type", "text");
        content.put("text", "Prompt: " + name);
        msg.put("content", content);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("description", name);
        result.put("messages", Collections.singletonList(msg));
        return result;
    }

    // ── helpers ──

    private Map<String, Object> errorObj(int code, String message) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("code", code);
        err.put("message", message);
        return err;
    }

    private Map<String, Object> resourceItem(String uri, String name, String desc) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("uri", uri);
        r.put("name", name);
        r.put("description", desc);
        r.put("mimeType", "text/plain");
        return r;
    }

    private Map<String, Object> promptItem(String name, String desc, Map<String, Object>... args) {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("name", name);
        p.put("description", desc);
        p.put("arguments", Arrays.asList(args));
        return p;
    }

    private Map<String, Object> argItem(String name, String desc, boolean required) {
        Map<String, Object> a = new LinkedHashMap<>();
        a.put("name", name);
        a.put("description", desc);
        a.put("required", required);
        return a;
    }

    private static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2) {
        Map<K, V> m = new LinkedHashMap<>();
        m.put(k1, v1);
        m.put(k2, v2);
        return m;
    }

    private static <K, V> Map<K, V> mapOf(K k, V v) {
        Map<K, V> m = new LinkedHashMap<>();
        m.put(k, v);
        return m;
    }
}
