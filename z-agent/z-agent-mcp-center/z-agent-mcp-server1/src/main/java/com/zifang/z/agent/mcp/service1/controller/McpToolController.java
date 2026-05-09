package com.zifang.z.agent.mcp.service1.controller;

import com.zifang.z.agent.mcp.core.McpRegistry;
import com.zifang.z.agent.mcp.core.ToolMeta;
import com.zifang.z.agent.mcp.service1.ToolRegisterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * MCP Tool 管理控制器
 */
@RestController
@RequestMapping("/v1/tools")
public class McpToolController {

    private static final Logger logger = LoggerFactory.getLogger(McpToolController.class);

    @Autowired
    private McpRegistry mcpRegistry;

    /**
     * 获取所有工具列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listTools(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") int limit) {

        List<ToolMeta> tools = mcpRegistry.listTools(filter);

        // 分页处理
        int start = 0;
        if (cursor != null) {
            try {
                start = Integer.parseInt(cursor);
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        int end = Math.min(start + limit, tools.size());
        List<Map<String, Object>> result = new ArrayList<>();

        for (int i = start; i < end; i++) {
            ToolMeta tool = tools.get(i);
            Map<String, Object> toolInfo = new HashMap<>();
            toolInfo.put("name", tool.getToolName());
            toolInfo.put("description", tool.getDescription());
            toolInfo.put("inputSchema", tool.getInputSchema());
            toolInfo.put("type", tool.getType());
            result.add(toolInfo);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("tools", result);
        response.put("total", tools.size());

        // 下一页游标
        if (end < tools.size()) {
            response.put("nextCursor", String.valueOf(end));
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 获取单个工具详情
     */
    @GetMapping("/{name}")
    public ResponseEntity<?> getTool(@PathVariable String name) {
        ToolMeta tool = mcpRegistry.getToolMeta(name);
        if (tool == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("name", tool.getToolName());
        result.put("description", tool.getDescription());
        result.put("inputSchema", tool.getInputSchema());
        result.put("outputSchema", tool.getOutputSchema());
        result.put("type", tool.getType());
        result.put("executeUrl", tool.getExecuteUrl());

        return ResponseEntity.ok(result);
    }

    /**
     * 注册新工具（第三方工具）
     */
    @PostMapping("/registry")
    public ResponseEntity<Map<String, Object>> registerTool(@RequestBody ToolRegisterRequest request) {
        logger.info("Registering tool: {}", request.getToolName());

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

            boolean success = mcpRegistry.registerTool(toolMeta);

            if (success) {
                result.put("success", true);
                result.put("message", "Tool registered successfully: " + request.getToolName());
                result.put("toolName", request.getToolName());
                return ResponseEntity.ok(result);
            } else {
                result.put("success", false);
                result.put("message", "Tool already exists: " + request.getToolName());
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            logger.error("Error registering tool: {}", request.getToolName(), e);
            result.put("success", false);
            result.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 注销工具
     */
    @DeleteMapping("/registry/{toolName}")
    public ResponseEntity<Map<String, Object>> unregisterTool(@PathVariable String toolName) {
        logger.info("Unregistering tool: {}", toolName);

        Map<String, Object> result = new HashMap<>();
        boolean success = mcpRegistry.unregisterTool(toolName);

        if (success) {
            result.put("success", true);
            result.put("message", "Tool unregistered successfully: " + toolName);
            return ResponseEntity.ok(result);
        } else {
            result.put("success", false);
            result.put("message", "Tool not found or is built-in: " + toolName);
            return ResponseEntity.badRequest().body(result);
        }
    }
}
