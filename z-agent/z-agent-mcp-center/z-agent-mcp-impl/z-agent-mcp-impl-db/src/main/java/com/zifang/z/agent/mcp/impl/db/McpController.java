package com.zifang.z.agent.mcp.impl.db;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
public class McpController {

    // Claude 会调用这个接口
    @PostMapping("/mcp")
    public Map<String, Object> handleMcp(@RequestBody Map<String, Object> request) {
        String method = (String) request.get("method");

        // 1. 返回工具列表
        if ("tools/list".equals(method)) {
            return Map.of(
                    "jsonrpc", "2.0",
                    "result", Map.of(
                            "tools", Arrays.asList(
                                    tool("getProjectInfo", "获取SpringBoot项目信息"),
                                    tool("getApiList", "获取项目所有接口列表"),
                                    tool("getDbTables", "查询数据库所有表名")
                            )
                    )
            );
        }

        // 2. 执行工具调用
        if ("tools/call".equals(method)) {
            Map<String, Object> params = (Map) request.get("params");
            String toolName = (String) params.get("name");

            String result = switch (toolName) {
                case "getProjectInfo" -> "SpringBoot 3.2.5 | JDK 17 | 本地开发环境";
                case "getApiList" -> "GET /mcp\nGET /api/list\nPOST /api/test";
                case "getDbTables" -> "user\norder\nproduct\nlog";
                default -> "工具不存在";
            };

            return Map.of(
                    "jsonrpc", "2.0",
                    "result", Map.of(
                            "content", Collections.singletonList(
                                    Map.of("type", "text", "text", result)
                            )
                    )
            );
        }

        return Map.of("jsonrpc", "2.0", "result", Map.of());
    }

    private Map<String, Object> tool(String name, String desc) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("description", desc);
        return map;
    }
}