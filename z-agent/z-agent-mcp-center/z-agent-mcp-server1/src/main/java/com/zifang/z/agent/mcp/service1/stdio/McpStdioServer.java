package com.zifang.z.agent.mcp.service1.stdio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zifang.z.agent.mcp.core.McpRegistry;
import com.zifang.z.agent.mcp.core.ToolMeta;
import com.zifang.z.agent.mcp.service1.BuiltInToolExecutor;
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

@SpringBootApplication
@ComponentScan(basePackages = "com.zifang.z.agent.mcp.starter")
public class McpStdioServer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(McpStdioServer.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final McpRegistry registry;
    private final BuiltInToolExecutor builtInToolExecutor;

    private boolean running = false;
    private PrintWriter out;

    public McpStdioServer(McpRegistry registry, BuiltInToolExecutor builtInToolExecutor) {
        this.registry = registry;
        this.builtInToolExecutor = builtInToolExecutor;
    }

    public static void main(String[] args) {
        boolean stdioMode = Arrays.asList(args).contains("--stdio");
        if (stdioMode) {
            System.setProperty("spring.main.banner-mode", "off");
            System.setProperty("logging.level.root", "ERROR");
            System.setProperty("logging.level.com.zifang", "ERROR");
            System.setProperty("spring.output.ansi.enabled", "NEVER");
            System.setProperty("spring.main.web-application-type", "NONE");
            System.setProperty("logging.pattern.console", "");
        }
        SpringApplication.run(McpStdioServer.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (!Arrays.asList(args).contains("--stdio")) {
            logger.info("MCP Stdio Server started. Use --stdio for stdio mode.");
            return;
        }
        startStdioServer();
    }

    private void startStdioServer() {
        running = true;
        out = new PrintWriter(System.out, true);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while (running && (line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                handleRequest(line);
            }
        } catch (IOException e) {
            if (running) logger.error("Error reading from stdin", e);
        } finally {
            running = false;
        }
    }

    private void handleRequest(String json) {
        try {
            JsonNode req = objectMapper.readTree(json);
            String method = req.has("method") ? req.get("method").asText() : "";
            JsonNode idNode = req.get("id");

            ObjectNode resp = objectMapper.createObjectNode();
            resp.put("jsonrpc", "2.0");
            if (idNode != null) resp.set("id", idNode);

            switch (method) {
                case "initialize": handleInit(req, resp); break;
                case "ping": handlePing(resp); break;
                case "tools/list": handleToolsList(resp); break;
                case "tools/call": handleToolsCall(req, resp); break;
                case "resources/list": handleResourcesList(resp); break;
                case "resources/read": handleResourcesRead(req, resp); break;
                case "prompts/list": handlePromptsList(resp); break;
                case "prompts/get": handlePromptsGet(req, resp); break;
                case "shutdown": handleShutdown(resp); break;
                default: err(resp, -32601, "Method not found: " + method);
            }
            send(resp);
        } catch (Exception e) {
            err(null, -32603, "Internal error: " + e.getMessage());
        }
    }

    private void handleInit(JsonNode req, ObjectNode resp) {
        ObjectNode result = objectMapper.createObjectNode();
        result.put("protocolVersion", "2024-11-05");
        ObjectNode si = objectMapper.createObjectNode();
        si.put("name", "z-agent-mcp-server1");
        si.put("version", "1.0.0");
        result.set("serverInfo", si);
        ObjectNode caps = objectMapper.createObjectNode();
        ObjectNode t = objectMapper.createObjectNode(); t.put("listChanged", false); caps.set("tools", t);
        ObjectNode r = objectMapper.createObjectNode(); r.put("subscribe", false); r.put("listChanged", false); caps.set("resources", r);
        ObjectNode p = objectMapper.createObjectNode(); p.put("listChanged", false); caps.set("prompts", p);
        result.set("capabilities", caps);
        resp.set("result", result);
    }

    private void handlePing(ObjectNode resp) {
        resp.set("result", objectMapper.createObjectNode());
    }

    private void handleToolsList(ObjectNode resp) {
        ArrayNode arr = objectMapper.createArrayNode();
        for (ToolMeta tm : registry.listTools(null)) {
            ObjectNode tn = objectMapper.createObjectNode();
            tn.put("name", tm.getToolName());
            tn.put("description", tm.getDescription());
            if (tm.getInputSchema() != null) tn.set("inputSchema", objectMapper.valueToTree(tm.getInputSchema()));
            arr.add(tn);
        }
        ObjectNode result = objectMapper.createObjectNode();
        result.set("tools", arr);
        result.putNull("nextCursor");
        resp.set("result", result);
    }

    @SuppressWarnings("unchecked")
    private void handleToolsCall(JsonNode req, ObjectNode resp) {
        JsonNode params = req.get("params");
        if (params == null) { err(resp, -32602, "Missing params"); return; }
        String name = params.has("name") ? params.get("name").asText() : null;
        if (name == null) { err(resp, -32602, "Missing tool name"); return; }
        if (registry.getToolMeta(name) == null) { err(resp, -32602, "Tool not found: " + name); return; }

        Map<String, Object> args = new HashMap<>();
        if (params.has("arguments")) {
            try { args = objectMapper.convertValue(params.get("arguments"), Map.class); } catch (Exception ignored) {}
        }

        Map<String, Object> execR;
        if (registry.isBuiltInTool(name) && builtInToolExecutor.supports(name)) {
            execR = builtInToolExecutor.execute(name, args);
        } else {
            execR = new HashMap<>();
            execR.put("content", Collections.singletonList(map("type","text","text","Tool '"+name+"' cannot be executed via stdio.")));
            execR.put("isError", true);
        }

        ObjectNode result = objectMapper.createObjectNode();
        result.put("isError", execR.containsKey("isError") ? (boolean) execR.get("isError") : false);
        result.set("content", objectMapper.valueToTree(execR.get("content")));
        resp.set("result", result);
    }

    private void handleResourcesList(ObjectNode resp) {
        ArrayNode arr = objectMapper.createArrayNode();
        arr.add(res("project://modules", "Project Modules", "Module list"));
        arr.add(res("project://status", "Server Status", "Status info"));
        ObjectNode result = objectMapper.createObjectNode();
        result.set("resources", arr);
        result.putNull("nextCursor");
        resp.set("result", result);
    }

    private void handleResourcesRead(JsonNode req, ObjectNode resp) {
        JsonNode params = req.get("params");
        if (params == null || !params.has("uri")) { err(resp, -32602, "Missing uri"); return; }
        String uri = params.get("uri").asText();
        ArrayNode contents = objectMapper.createArrayNode();
        ObjectNode c = objectMapper.createObjectNode();
        c.put("uri", uri);
        c.put("mimeType", "text/plain");
        switch (uri) {
            case "project://modules": c.put("text", buildModulesText()); break;
            case "project://status": c.put("text", buildStatusText()); break;
            default: c.put("text", "Resource not found: " + uri);
        }
        contents.add(c);
        ObjectNode result = objectMapper.createObjectNode();
        result.set("contents", contents);
        resp.set("result", result);
    }

    private void handlePromptsList(ObjectNode resp) {
        ArrayNode arr = objectMapper.createArrayNode();
        arr.add(prompt("code_review", "Code review template", arg("file_path", "File path", true)));
        arr.add(prompt("system_design", "System design discussion", arg("topic", "Design topic", true)));
        ObjectNode result = objectMapper.createObjectNode();
        result.set("prompts", arr);
        result.putNull("nextCursor");
        resp.set("result", result);
    }

    private void handlePromptsGet(JsonNode req, ObjectNode resp) {
        JsonNode params = req.get("params");
        if (params == null || !params.has("name")) { err(resp, -32602, "Missing prompt name"); return; }
        String name = params.get("name").asText();
        ArrayNode msgs = objectMapper.createArrayNode();
        ObjectNode msg = objectMapper.createObjectNode();
        msg.put("role", "user");
        ObjectNode mc = objectMapper.createObjectNode();
        mc.put("type", "text");
        switch (name) {
            case "code_review":
                mc.put("text", "Review code. Focus on quality, bugs, performance, security, architecture.");
                break;
            case "system_design":
                mc.put("text", "Let's discuss system design. Consider scalability, data model, API, fault tolerance.");
                break;
            default: mc.put("text", "Prompt not found: " + name);
        }
        msg.set("content", mc);
        msgs.add(msg);
        ObjectNode result = objectMapper.createObjectNode();
        result.put("description", name);
        result.set("messages", msgs);
        resp.set("result", result);
    }

    private void handleShutdown(ObjectNode resp) {
        resp.set("result", objectMapper.createObjectNode());
        send(resp);
        running = false;
    }

    private void send(ObjectNode resp) {
        try {
            out.println(objectMapper.writeValueAsString(resp));
            out.flush();
        } catch (Exception e) {
            System.err.println("[McpStdioServer] send error: " + e.getMessage());
        }
    }

    private void err(ObjectNode resp, int code, String msg) {
        if (resp == null) { resp = objectMapper.createObjectNode(); resp.put("jsonrpc", "2.0"); resp.putNull("id"); }
        ObjectNode e = objectMapper.createObjectNode();
        e.put("code", code);
        e.put("message", msg);
        resp.set("error", e);
        send(resp);
    }

    private ObjectNode res(String uri, String name, String desc) {
        ObjectNode r = objectMapper.createObjectNode();
        r.put("uri", uri); r.put("name", name); r.put("description", desc); r.put("mimeType", "text/plain");
        return r;
    }

    private ObjectNode prompt(String name, String desc, ObjectNode... args) {
        ObjectNode p = objectMapper.createObjectNode();
        p.put("name", name); p.put("description", desc);
        ArrayNode a = objectMapper.createArrayNode();
        for (ObjectNode arg : args) a.add(arg);
        p.set("arguments", a);
        return p;
    }

    private ObjectNode arg(String name, String desc, boolean required) {
        ObjectNode a = objectMapper.createObjectNode();
        a.put("name", name); a.put("description", desc); a.put("required", required);
        return a;
    }

    private Map<String, Object> map(String k1, Object v1, String k2, Object v2) {
        Map<String, Object> m = new HashMap<>();
        m.put(k1, v1); m.put(k2, v2);
        return m;
    }

    private String buildModulesText() {
        return "=== z-one-company Modules ===\n\n"
            + "Bootstraps:\n  - main-starter (8080)\n\n"
            + "Core: z-ctc(8092) z-config(8848) z-task(8090) z-wf(8091) z-schedule z-mist z-meta z-oss z-gw z-mq z-rpc\n\n"
            + "Agent: z-agent-mcp-server1 (this server)\n";
    }

    private String buildStatusText() {
        return "=== Status ===\nRunning | MCP 2024-11-05 | stdio\n"
            + "Tools: " + registry.listTools(null).size() + "\n"
            + "Java: " + System.getProperty("java.version") + "\n";
    }
}
