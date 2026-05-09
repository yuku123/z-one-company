     1|package com.zifang.z.agent.mcp.starter.stdio;
     2|
     3|import com.fasterxml.jackson.databind.JsonNode;
     4|import com.fasterxml.jackson.databind.ObjectMapper;
     5|import com.fasterxml.jackson.databind.node.ArrayNode;
     6|import com.fasterxml.jackson.databind.node.ObjectNode;
     7|import com.zifang.z.agent.mcp.starter.McpHandler;
     8|import com.zifang.z.agent.mcp.core.McpRegistry;
     9|import com.zifang.z.agent.mcp.core.ToolMeta;
    10|import com.zifang.z.agent.mcp.starter.BuiltInToolExecutor;
    11|import org.slf4j.Logger;
    12|import org.slf4j.LoggerFactory;
    13|import org.springframework.boot.CommandLineRunner;
    14|import org.springframework.boot.SpringApplication;
    15|import org.springframework.boot.autoconfigure.SpringBootApplication;
    16|import org.springframework.context.ConfigurableApplicationContext;
    17|import org.springframework.context.annotation.ComponentScan;
    18|
    19|import java.io.BufferedReader;
    20|import java.io.IOException;
    21|import java.io.InputStreamReader;
    22|import java.io.PrintWriter;
    23|import java.util.*;
    24|
    25|/**
    26| * MCP Stdio Server — 标准 JSON-RPC over stdio 传输
    27| *
    28| * 完全符合 MCP 2024-11-05 协议规范：
    29| *   - initialize (握手协商)
    30| *   - tools/list, tools/call
    31| *   - resources/list, resources/read
    32| *   - prompts/list, prompts/get
    33| *   - ping, shutdown
    34| *
    35| * 使用方法:
    36| *   java -jar z-agent-mcp-server1-*.jar --stdio
    37| */
    38|@SpringBootApplication
    39|@ComponentScan(basePackages = "com.zifang.z.agent.mcp.starter")
    40|public class McpStdioServer implements CommandLineRunner {
    41|
    42|    private static final Logger logger = LoggerFactory.getLogger(McpStdioServer.class);
    43|    private static final ObjectMapper objectMapper = new ObjectMapper();
    44|
    45|    private final McpRegistry registry;
    46|    private final BuiltInToolExecutor builtInToolExecutor;
    47|
    48|    private boolean running = false;
    49|    private PrintWriter out;
    50|    private ConfigurableApplicationContext ctx;
    51|
    52|    public McpStdioServer(McpRegistry registry, BuiltInToolExecutor builtInToolExecutor) {
    53|        this.registry = registry;
    54|        this.builtInToolExecutor = builtInToolExecutor;
    55|    }
    56|
    57|    public static void main(String[] args) {
    58|        boolean stdioMode = Arrays.asList(args).contains("--stdio");
    59|        if (stdioMode) {
    60|            System.setProperty("spring.main.banner-mode", "off");
    61|            System.setProperty("logging.level.root", "ERROR");
    62|            System.setProperty("logging.level.com.zifang", "ERROR");
    63|            // 禁止 Spring Boot 日志输出到 stdout
    64|            System.setProperty("spring.output.ansi.enabled", "NEVER");
    65|            // 关键：stdio 模式下不启动 web server
    66|            System.setProperty("spring.main.web-application-type", "NONE");
    67|            // 禁止所有日志输出到控制台
    68|            System.setProperty("logging.pattern.console", "");
    69|        }
    70|        SpringApplication.run(McpStdioServer.class, args);
    71|    }
    72|
    73|    @Override
    74|    public void run(String... args) throws Exception {
    75|        boolean stdioMode = Arrays.asList(args).contains("--stdio");
    76|        if (!stdioMode) {
    77|            logger.info("MCP Stdio Server started. Use --stdio for stdio mode.");
    78|            return;
    79|        }
    80|        startStdioServer();
    81|    }
    82|
    83|    private void startStdioServer() {
    84|        running = true;
    85|        out = new PrintWriter(System.out, true);
    86|        // 注意：MCP 协议规定客户端先发 initialize，服务端不能先发消息
    87|        // 不发送 server/ready 通知
    88|
    89|        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
    90|            String line;
    91|            while (running && (line = reader.readLine()) != null) {
    92|                if (line.trim().isEmpty()) continue;
    93|                handleRequest(line);
    94|            }
    95|        } catch (IOException e) {
    96|            if (running) {
    97|                logger.error("Error reading from stdin", e);
    98|            }
    99|        } finally {
   100|            running = false;
   101|        }
   102|    }
   103|
   104|    private void handleRequest(String json) {
   105|        try {
   106|            JsonNode requestNode = objectMapper.readTree(json);
   107|            String method = requestNode.has("method") ? requestNode.get("method").asText() : "";
   108|            JsonNode idNode = requestNode.get("id");
   109|
   110|            ObjectNode response = objectMapper.createObjectNode();
   111|            response.put("jsonrpc", "2.0");
   112|            if (idNode != null) {
   113|                response.set("id", idNode);
   114|            }
   115|
   116|            switch (method) {
   117|                case "initialize":
   118|                    handleInitialize(requestNode, response);
   119|                    break;
   120|                case "ping":
   121|                    handlePing(response);
   122|                    break;
   123|                case "tools/list":
   124|                    handleToolsList(response);
   125|                    break;
   126|                case "tools/call":
   127|                    handleToolsCall(requestNode, response);
   128|                    break;
   129|                case "resources/list":
   130|                    handleResourcesList(response);
   131|                    break;
   132|                case "resources/read":
   133|                    handleResourcesRead(requestNode, response);
   134|                    break;
   135|                case "prompts/list":
   136|                    handlePromptsList(response);
   137|                    break;
   138|                case "prompts/get":
   139|                    handlePromptsGet(requestNode, response);
   140|                    break;
   141|                case "shutdown":
   142|                    handleShutdown(response);
   143|                    break;
   144|                default:
   145|                    handleUnknownMethod(method, response);
   146|            }
   147|
   148|            sendResponse(response);
   149|
   150|        } catch (Exception e) {
   151|            sendErrorResponse(null, -32603, "Internal error: " + e.getMessage());
   152|        }
   153|    }
   154|
   155|    // ──── initialize ────
   156|    private void handleInitialize(JsonNode request, ObjectNode response) {
   157|        ObjectNode result = objectMapper.createObjectNode();
   158|        result.put("protocolVersion", "2024-11-05");
   159|
   160|        ObjectNode serverInfo = objectMapper.createObjectNode();
   161|        serverInfo.put("name", "z-agent-mcp-server1");
   162|        serverInfo.put("version", "1.0.0");
   163|        result.set("serverInfo", serverInfo);
   164|
   165|        ObjectNode capabilities = objectMapper.createObjectNode();
   166|
   167|        ObjectNode tools = objectMapper.createObjectNode();
   168|        tools.put("listChanged", false);
   169|        capabilities.set("tools", tools);
   170|
   171|        ObjectNode resources = objectMapper.createObjectNode();
   172|        resources.put("subscribe", false);
   173|        resources.put("listChanged", false);
   174|        capabilities.set("resources", resources);
   175|
   176|        ObjectNode prompts = objectMapper.createObjectNode();
   177|        prompts.put("listChanged", false);
   178|        capabilities.set("prompts", prompts);
   179|
   180|        result.set("capabilities", capabilities);
   181|        response.set("result", result);
   182|    }
   183|
   184|    private void handlePing(ObjectNode response) {
   185|        ObjectNode result = objectMapper.createObjectNode();
   186|        response.set("result", result);
   187|    }
   188|
   189|    // ──── tools/list ────
   190|    private void handleToolsList(ObjectNode response) {
   191|        List<ToolMeta> tools = registry.listTools(null);
   192|        ArrayNode toolsArray = objectMapper.createArrayNode();
   193|        for (ToolMeta tool : tools) {
   194|            ObjectNode toolNode = objectMapper.createObjectNode();
   195|            toolNode.put("name", tool.getToolName());
   196|            toolNode.put("description", tool.getDescription());
   197|            if (tool.getInputSchema() != null) {
   198|                toolNode.set("inputSchema", objectMapper.valueToTree(tool.getInputSchema()));
   199|            }
   200|            toolsArray.add(toolNode);
   201|        }
   202|        ObjectNode result = objectMapper.createObjectNode();
   203|        result.set("tools", toolsArray);
   204|        result.putNull("nextCursor");
   205|        response.set("result", result);
   206|    }
   207|
   208|    // ──── tools/call ────
   209|    private void handleToolsCall(JsonNode request, ObjectNode response) {
   210|        JsonNode paramsNode = request.get("params");
   211|        if (paramsNode == null) {
   212|            sendErrorResponse(response, -32602, "Missing params");
   213|            return;
   214|        }
   215|
   216|        String toolName = paramsNode.has("name") ? paramsNode.get("name").asText() : null;
   217|        if (toolName == null) {
   218|            sendErrorResponse(response, -32602, "Missing tool name");
   219|            return;
   220|        }
   221|
   222|        ToolMeta toolMeta = registry.getToolMeta(toolName);
   223|        if (toolMeta == null) {
   224|            sendErrorResponse(response, -32602, "Tool not found: " + toolName);
   225|            return;
   226|        }
   227|
   228|        // 解析 arguments
   229|        Map<String, Object> arguments = new HashMap<>();
   230|        if (paramsNode.has("arguments")) {
   231|            try {
   232|                @SuppressWarnings("unchecked")
   233|                Map<String, Object> parsed = objectMapper.convertValue(paramsNode.get("arguments"), Map.class);
   234|                arguments = parsed;
   235|            } catch (Exception e) {
   236|                // 忽略解析错误，使用空 map
   237|            }
   238|        }
   239|
   240|        // 执行工具
   241|        Map<String, Object> execResult;
   242|        if (registry.isBuiltInTool(toolName) && builtInToolExecutor.supports(toolName)) {
   243|            execResult = builtInToolExecutor.execute(toolName, arguments);
   244|        } else {
   245|            // 非内置工具：返回未实现
   246|            execResult = new HashMap<>();
   247|            List<Map<String, Object>> content = new ArrayList<>();
   248|            Map<String, Object> item = new HashMap<>();
   249|            item.put("type", "text");
   250|            item.put("text", "Tool '" + toolName + "' is not a built-in tool and cannot be executed via stdio.");
   251|            content.add(item);
   252|            execResult.put("content", content);
   253|            execResult.put("isError", true);
   254|        }
   255|
   256|        ObjectNode result = objectMapper.createObjectNode();
   257|        result.put("isError", execResult.containsKey("isError") ? (boolean) execResult.get("isError") : false);
   258|        result.set("content", objectMapper.valueToTree(execResult.get("content")));
   259|        response.set("result", result);
   260|    }
   261|
   262|    // ──── resources/list ────
   263|    private void handleResourcesList(ObjectNode response) {
   264|        ArrayNode resourcesArray = objectMapper.createArrayNode();
   265|
   266|        // 资源: project://modules
   267|        ObjectNode res1 = objectMapper.createObjectNode();
   268|        res1.put("uri", "project://modules");
   269|        res1.put("name", "Project Modules");
   270|        res1.put("description", "List of all z-one-company modules");
   271|        res1.put("mimeType", "text/plain");
   272|        resourcesArray.add(res1);
   273|
   274|        // 资源: project://status
   275|        ObjectNode res2 = objectMapper.createObjectNode();
   276|        res2.put("uri", "project://status");
   277|        res2.put("name", "Server Status");
   278|        res2.put("description", "Current server status information");
   279|        res2.put("mimeType", "text/plain");
   280|        resourcesArray.add(res2);
   281|
   282|        ObjectNode result = objectMapper.createObjectNode();
   283|        result.set("resources", resourcesArray);
   284|        result.putNull("nextCursor");
   285|        response.set("result", result);
   286|    }
   287|
   288|    // ──── resources/read ────
   289|    private void handleResourcesRead(JsonNode request, ObjectNode response) {
   290|        JsonNode paramsNode = request.get("params");
   291|        if (paramsNode == null || !paramsNode.has("uri")) {
   292|            sendErrorResponse(response, -32602, "Missing uri");
   293|            return;
   294|        }
   295|
   296|        String uri = paramsNode.get("uri").asText();
   297|        ArrayNode contents = objectMapper.createArrayNode();
   298|        ObjectNode content = objectMapper.createObjectNode();
   299|
   300|        switch (uri) {
   301|            case "project://modules":
   302|                content.put("uri", uri);
   303|                content.put("mimeType", "text/plain");
   304|                content.put("text", buildModulesText());
   305|                break;
   306|            case "project://status":
   307|                content.put("uri", uri);
   308|                content.put("mimeType", "text/plain");
   309|                content.put("text", buildStatusText());
   310|                break;
   311|            default:
   312|                content.put("uri", uri);
   313|                content.put("mimeType", "text/plain");
   314|                content.put("text", "Resource not found: " + uri);
   315|                break;
   316|        }
   317|        contents.add(content);
   318|
   319|        ObjectNode result = objectMapper.createObjectNode();
   320|        result.set("contents", contents);
   321|        response.set("result", result);
   322|    }
   323|
   324|    // ──── prompts/list ────
   325|    private void handlePromptsList(ObjectNode response) {
   326|        ArrayNode promptsArray = objectMapper.createArrayNode();
   327|
   328|        ObjectNode prompt1 = objectMapper.createObjectNode();
   329|        prompt1.put("name", "code_review");
   330|        prompt1.put("description", "Code review prompt template");
   331|
   332|        ArrayNode args1 = objectMapper.createArrayNode();
   333|        ObjectNode arg1 = objectMapper.createObjectNode();
   334|        arg1.put("name", "file_path");
   335|        arg1.put("description", "Path to the file to review");
   336|        arg1.put("required", true);
   337|        args1.add(arg1);
   338|        prompt1.set("arguments", args1);
   339|        promptsArray.add(prompt1);
   340|
   341|        ObjectNode prompt2 = objectMapper.createObjectNode();
   342|        prompt2.put("name", "system_design");
   343|        prompt2.put("description", "System design discussion prompt");
   344|        ArrayNode args2 = objectMapper.createArrayNode();
   345|        ObjectNode arg2 = objectMapper.createObjectNode();
   346|        arg2.put("name", "topic");
   347|        arg2.put("description", "Design topic to discuss");
   348|        arg2.put("required", true);
   349|        args2.add(arg2);
   350|        prompt2.set("arguments", args2);
   351|        promptsArray.add(prompt2);
   352|
   353|        ObjectNode result = objectMapper.createObjectNode();
   354|        result.set("prompts", promptsArray);
   355|        result.putNull("nextCursor");
   356|        response.set("result", result);
   357|    }
   358|
   359|    // ──── prompts/get ────
   360|    private void handlePromptsGet(JsonNode request, ObjectNode response) {
   361|        JsonNode paramsNode = request.get("params");
   362|        if (paramsNode == null || !paramsNode.has("name")) {
   363|            sendErrorResponse(response, -32602, "Missing prompt name");
   364|            return;
   365|        }
   366|
   367|        String name = paramsNode.get("name").asText();
   368|        String filePath = paramsNode.has("arguments") && paramsNode.get("arguments").has("file_path")
   369|                ? paramsNode.get("arguments").get("file_path").asText()
   370|                : null;
   371|
   372|        ArrayNode messages = objectMapper.createArrayNode();
   373|        ObjectNode message = objectMapper.createObjectNode();
   374|        message.put("role", "user");
   375|
   376|        ObjectNode msgContent = objectMapper.createObjectNode();
   377|        msgContent.put("type", "text");
   378|
   379|        switch (name) {
   380|            case "code_review":
   381|                msgContent.put("text", "Please review the following code file: "
   382|                        + (filePath != null ? filePath : "<file_path>")
   383|                        + "\n\nFocus on:\n1. Code quality and readability\n2. Potential bugs\n3. Performance issues\n4. Security concerns\n5. Architecture compliance");
   384|                break;
   385|            case "system_design":
   386|                String topic = paramsNode.has("arguments") && paramsNode.get("arguments").has("topic")
   387|                        ? paramsNode.get("arguments").get("topic").asText()
   388|                        : "<topic>";
   389|                msgContent.put("text", "Let's discuss the system design for: " + topic
   390|                        + "\n\nConsider:\n1. Scalability requirements\n2. Data model\n3. API design\n4. Fault tolerance\n5. Deployment strategy");
   391|                break;
   392|            default:
   393|                msgContent.put("text", "Prompt not found: " + name);
   394|                break;
   395|        }
   396|
   397|        message.set("content", msgContent);
   398|        messages.add(message);
   399|
   400|        ObjectNode result = objectMapper.createObjectNode();
   401|        result.put("description", name + " prompt");
   402|        result.set("messages", messages);
   403|        response.set("result", result);
   404|    }
   405|
   406|    private void handleShutdown(ObjectNode response) {
   407|        response.set("result", objectMapper.createObjectNode());
   408|        sendResponse(response);
   409|        running = false;
   410|    }
   411|
   412|    private void handleUnknownMethod(String method, ObjectNode response) {
   413|        sendErrorResponse(response, -32601, "Method not found: " + method);
   414|    }
   415|
   416|    private void sendResponse(ObjectNode response) {
   417|        try {
   418|            String json = objectMapper.writeValueAsString(response);
   419|            // 每个 JSON-RPC 消息一行
   420|            out.println(json);
   421|            out.flush();
   422|        } catch (Exception e) {
   423|            // 写入stderr避免污染stdout
   424|            System.err.println("[McpStdioServer] Error sending response: " + e.getMessage());
   425|        }
   426|    }
   427|
   428|    private void sendErrorResponse(ObjectNode response, int code, String message) {
   429|        if (response == null) {
   430|            response = objectMapper.createObjectNode();
   431|            response.put("jsonrpc", "2.0");
   432|            response.putNull("id");
   433|        }
   434|        ObjectNode error = objectMapper.createObjectNode();
   435|        error.put("code", code);
   436|        error.put("message", message);
   437|        response.set("error", error);
   438|        sendResponse(response);
   439|    }
   440|
   441|    // ──── resource content builders ────
   442|
   443|    private String buildModulesText() {
   444|        StringBuilder sb = new StringBuilder();
   445|        sb.append("=== z-one-company Modules ===\n\n");
   446|        sb.append("Bootstraps:\n");
   447|        sb.append("  - z-one-company-main-starter (port 8080)\n\n");
   448|        sb.append("Core Services:\n");
   449|        sb.append("  - z-ctc      - Auth & permissions (port 8092)\n");
   450|        sb.append("  - z-config   - Config center (port 8848)\n");
   451|        sb.append("  - z-task     - Task center (port 8090)\n");
   452|        sb.append("  - z-wf       - Workflow engine (port 8091)\n");
   453|        sb.append("  - z-schedule - Scheduling center\n");
   454|        sb.append("  - z-mist     - Secrets management\n");
   455|        sb.append("  - z-meta     - Metadata management\n");
   456|        sb.append("  - z-oss      - Object storage\n");
   457|        sb.append("  - z-gw       - API Gateway\n");
   458|        sb.append("  - z-mq       - Message queue\n");
   459|        sb.append("  - z-rpc      - RPC framework\n\n");
   460|        sb.append("Agent:\n  - z-agent-mcp-server1 (this MCP server)\n");
   461|        return sb.toString();
   462|    }
   463|
   464|    private String buildStatusText() {
   465|        StringBuilder sb = new StringBuilder();
   466|        sb.append("=== z-agent-mcp-server1 Status ===\n");
   467|        sb.append("Status: RUNNING\n");
   468|        sb.append("Protocol: MCP 2024-11-05\n");
   469|        sb.append("Transport: stdio\n");
   470|        sb.append("Registered Tools: ").append(registry.listTools(null).size()).append("\n");
   471|        sb.append("Java: ").append(System.getProperty("java.version")).append("\n");
   472|        sb.append("OS: ").append(System.getProperty("os.name")).append("\n");
   473|        sb.append("Time: ").append(new Date()).append("\n");
   474|        return sb.toString();
   475|    }
   476|}
   477|