package com.zifang.z.agent.mcp.service1;

import com.zifang.z.agent.mcp.core.ToolExecutor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 内置工具执行器 — 实现可用的 MCP 基础工具
 */
@Component
public class BuiltInToolExecutor implements ToolExecutor {

    private static final Set<String> SUPPORTED_TOOLS = new HashSet<>(Arrays.asList(
            "echo",
            "get_time",
            "system_info",
            "list_modules",
            "generate_uuid"
    ));

    @Override
    public boolean supports(String toolName) {
        return SUPPORTED_TOOLS.contains(toolName);
    }

    @Override
    public Map<String, Object> execute(String toolName, Map<String, Object> arguments) {
        switch (toolName) {
            case "echo":
                return doEcho(arguments);
            case "get_time":
                return doGetTime(arguments);
            case "system_info":
                return doSystemInfo(arguments);
            case "list_modules":
                return doListModules(arguments);
            case "generate_uuid":
                return doGenerateUuid(arguments);
            default:
                return buildError("Unknown built-in tool: " + toolName);
        }
    }

    // ──── echo ────
    private Map<String, Object> doEcho(Map<String, Object> args) {
        String message = args != null ? Objects.toString(args.get("message"), "") : "";
        StringBuilder sb = new StringBuilder();
        sb.append("Echo: ").append(message).append("\n");
        sb.append("arg count: ").append(args != null ? args.size() : 0).append("\n");
        if (args != null && !args.isEmpty()) {
            sb.append("args: ").append(args).append("\n");
        }
        return buildTextResult(sb.toString());
    }

    // ──── get_time ────
    private Map<String, Object> doGetTime(Map<String, Object> args) {
        String tz = args != null ? Objects.toString(args.get("timezone"), "Asia/Shanghai") : "Asia/Shanghai";
        Instant now = Instant.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                .withZone(ZoneId.of(tz));

        StringBuilder sb = new StringBuilder();
        sb.append("Current time: ").append(fmt.format(now)).append("\n");
        sb.append("Timezone: ").append(tz).append("\n");
        sb.append("Unix ms: ").append(now.toEpochMilli()).append("\n");
        sb.append("ISO: ").append(now.toString()).append("\n");
        return buildTextResult(sb.toString());
    }

    // ──── system_info ────
    private Map<String, Object> doSystemInfo(Map<String, Object> args) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== System Info ===\n");
        sb.append("Java Version: ").append(System.getProperty("java.version")).append("\n");
        sb.append("Java Vendor: ").append(System.getProperty("java.vendor")).append("\n");
        sb.append("OS: ").append(System.getProperty("os.name"))
                .append(" ").append(System.getProperty("os.version"))
                .append(" (").append(System.getProperty("os.arch")).append(")\n");
        sb.append("Available Processors: ").append(Runtime.getRuntime().availableProcessors()).append("\n");
        sb.append("Max Memory: ").append(Runtime.getRuntime().maxMemory() / 1024 / 1024).append(" MB\n");
        sb.append("Server: z-agent-mcp-server1\n");
        sb.append("Version: 1.0.0\n");
        return buildTextResult(sb.toString());
    }

    // ──── list_modules ────
    private Map<String, Object> doListModules(Map<String, Object> args) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== z-one-company Modules ===\n\n");
        sb.append("Bootstraps:\n");
        sb.append("  - z-one-company-main-starter (port 8080)\n");
        sb.append("  - z-one-company-main-starter-frontend (port 3000)\n\n");
        sb.append("Core Services:\n");
        sb.append("  - z-ctc      - Auth & permissions (port 8092)\n");
        sb.append("  - z-config   - Config center (port 8848)\n");
        sb.append("  - z-task     - Task center (port 8090)\n");
        sb.append("  - z-wf       - Workflow engine (port 8091)\n");
        sb.append("  - z-schedule - Scheduling center\n");
        sb.append("  - z-mist     - Secrets management (port 8085)\n");
        sb.append("  - z-meta     - Metadata management (port 8093)\n");
        sb.append("  - z-oss      - Object storage\n");
        sb.append("  - z-gw       - API Gateway\n");
        sb.append("  - z-mq       - Message queue\n");
        sb.append("  - z-rpc      - RPC framework\n");
        sb.append("  - z-ext      - Extensions\n\n");
        sb.append("Agent:\n");
        sb.append("  - z-agent        - Agent framework\n");
        sb.append("  - z-agent-mcp-server1 - MCP Server (this)\n\n");
        sb.append("Infrastructure:\n");
        sb.append("  - z-boot    - Spring Boot starters\n");
        sb.append("  - z-cache   - Cache layer\n");
        return buildTextResult(sb.toString());
    }

    // ──── generate_uuid ────
    private Map<String, Object> doGenerateUuid(Map<String, Object> args) {
        int count = 1;
        if (args != null && args.containsKey("count")) {
            try {
                count = Integer.parseInt(args.get("count").toString());
                count = Math.max(1, Math.min(10, count));
            } catch (NumberFormatException ignored) {
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(UUID.randomUUID().toString()).append("\n");
        }
        return buildTextResult(sb.toString().trim());
    }

    // ──── helpers ────

    private Map<String, Object> buildTextResult(String text) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> content = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("type", "text");
        item.put("text", text);
        content.add(item);
        result.put("content", content);
        result.put("isError", false);
        return result;
    }

    private Map<String, Object> buildError(String message) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> content = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("type", "text");
        item.put("text", "Error: " + message);
        content.add(item);
        result.put("content", content);
        result.put("isError", true);
        return result;
    }
}
