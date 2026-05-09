     1|package com.zifang.z.agent.mcp.starter.controller;
     2|
     3|import com.zifang.z.agent.mcp.core.McpRegistry;
     4|import com.zifang.z.agent.mcp.core.ToolMeta;
     5|import com.zifang.z.agent.mcp.starter.ToolRegisterRequest;
     6|import org.slf4j.Logger;
     7|import org.slf4j.LoggerFactory;
     8|import org.springframework.beans.factory.annotation.Autowired;
     9|import org.springframework.http.ResponseEntity;
    10|import org.springframework.web.bind.annotation.*;
    11|
    12|import java.util.*;
    13|
    14|/**
    15| * MCP Tool 管理控制器
    16| */
    17|@RestController
    18|@RequestMapping("/v1/tools")
    19|public class McpToolController {
    20|
    21|    private static final Logger logger = LoggerFactory.getLogger(McpToolController.class);
    22|
    23|    @Autowired
    24|    private McpRegistry mcpRegistry;
    25|
    26|    /**
    27|     * 获取所有工具列表
    28|     */
    29|    @GetMapping
    30|    public ResponseEntity<Map<String, Object>> listTools(
    31|            @RequestParam(required = false) String filter,
    32|            @RequestParam(required = false) String cursor,
    33|            @RequestParam(defaultValue = "20") int limit) {
    34|
    35|        List<ToolMeta> tools = mcpRegistry.listTools(filter);
    36|
    37|        // 分页处理
    38|        int start = 0;
    39|        if (cursor != null) {
    40|            try {
    41|                start = Integer.parseInt(cursor);
    42|            } catch (NumberFormatException e) {
    43|                // ignore
    44|            }
    45|        }
    46|
    47|        int end = Math.min(start + limit, tools.size());
    48|        List<Map<String, Object>> result = new ArrayList<>();
    49|
    50|        for (int i = start; i < end; i++) {
    51|            ToolMeta tool = tools.get(i);
    52|            Map<String, Object> toolInfo = new HashMap<>();
    53|            toolInfo.put("name", tool.getToolName());
    54|            toolInfo.put("description", tool.getDescription());
    55|            toolInfo.put("inputSchema", tool.getInputSchema());
    56|            toolInfo.put("type", tool.getType());
    57|            result.add(toolInfo);
    58|        }
    59|
    60|        Map<String, Object> response = new HashMap<>();
    61|        response.put("tools", result);
    62|        response.put("total", tools.size());
    63|
    64|        // 下一页游标
    65|        if (end < tools.size()) {
    66|            response.put("nextCursor", String.valueOf(end));
    67|        }
    68|
    69|        return ResponseEntity.ok(response);
    70|    }
    71|
    72|    /**
    73|     * 获取单个工具详情
    74|     */
    75|    @GetMapping("/{name}")
    76|    public ResponseEntity<?> getTool(@PathVariable String name) {
    77|        ToolMeta tool = mcpRegistry.getToolMeta(name);
    78|        if (tool == null) {
    79|            return ResponseEntity.notFound().build();
    80|        }
    81|
    82|        Map<String, Object> result = new HashMap<>();
    83|        result.put("name", tool.getToolName());
    84|        result.put("description", tool.getDescription());
    85|        result.put("inputSchema", tool.getInputSchema());
    86|        result.put("outputSchema", tool.getOutputSchema());
    87|        result.put("type", tool.getType());
    88|        result.put("executeUrl", tool.getExecuteUrl());
    89|
    90|        return ResponseEntity.ok(result);
    91|    }
    92|
    93|    /**
    94|     * 注册新工具（第三方工具）
    95|     */
    96|    @PostMapping("/registry")
    97|    public ResponseEntity<Map<String, Object>> registerTool(@RequestBody ToolRegisterRequest request) {
    98|        logger.info("Registering tool: {}", request.getToolName());
    99|
   100|        Map<String, Object> result = new HashMap<>();
   101|
   102|        try {
   103|            ToolMeta toolMeta = new ToolMeta();
   104|            toolMeta.setToolName(request.getToolName());
   105|            toolMeta.setType("THIRD_PARTY");
   106|            toolMeta.setDescription(request.getDescription());
   107|            toolMeta.setInputSchema(request.getInputSchema());
   108|            toolMeta.setOutputSchema(request.getOutputSchema());
   109|            toolMeta.setExecuteUrl(request.getExecuteUrl());
   110|            toolMeta.setAuthToken(request.getAuthToken());
   111|
   112|            boolean success = mcpRegistry.registerTool(toolMeta);
   113|
   114|            if (success) {
   115|                result.put("success", true);
   116|                result.put("message", "Tool registered successfully: " + request.getToolName());
   117|                result.put("toolName", request.getToolName());
   118|                return ResponseEntity.ok(result);
   119|            } else {
   120|                result.put("success", false);
   121|                result.put("message", "Tool already exists: " + request.getToolName());
   122|                return ResponseEntity.badRequest().body(result);
   123|            }
   124|        } catch (Exception e) {
   125|            logger.error("Error registering tool: {}", request.getToolName(), e);
   126|            result.put("success", false);
   127|            result.put("message", "Registration failed: " + e.getMessage());
   128|            return ResponseEntity.badRequest().body(result);
   129|        }
   130|    }
   131|
   132|    /**
   133|     * 注销工具
   134|     */
   135|    @DeleteMapping("/registry/{toolName}")
   136|    public ResponseEntity<Map<String, Object>> unregisterTool(@PathVariable String toolName) {
   137|        logger.info("Unregistering tool: {}", toolName);
   138|
   139|        Map<String, Object> result = new HashMap<>();
   140|        boolean success = mcpRegistry.unregisterTool(toolName);
   141|
   142|        if (success) {
   143|            result.put("success", true);
   144|            result.put("message", "Tool unregistered successfully: " + toolName);
   145|            return ResponseEntity.ok(result);
   146|        } else {
   147|            result.put("success", false);
   148|            result.put("message", "Tool not found or is built-in: " + toolName);
   149|            return ResponseEntity.badRequest().body(result);
   150|        }
   151|    }
   152|}
   153|