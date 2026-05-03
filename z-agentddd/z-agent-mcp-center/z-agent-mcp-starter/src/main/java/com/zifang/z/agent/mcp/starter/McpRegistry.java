package com.zifang.z.agent.mcp.starter;


import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * MCP工具注册中心（线程安全）
 */
@Component
public class McpRegistry {
    // 存储：toolName -> ToolMeta
    private final Map<String, ToolMeta> toolMap = new ConcurrentHashMap<>();

    /**
     * 注册工具（内置/第三方）
     */
    public boolean registerTool(ToolMeta toolMeta) {
        if (toolMap.containsKey(toolMeta.getToolName())) {
            return false;
        }
        toolMeta.setCreateTime(System.currentTimeMillis());
        toolMap.put(toolMeta.getToolName(), toolMeta);
        return true;
    }

    /**
     * 注销工具（仅第三方）
     */
    public boolean unregisterTool(String toolName) {
        ToolMeta meta = toolMap.get(toolName);
        if (meta == null || "BUILT_IN".equals(meta.getType())) {
            return false;
        }
        toolMap.remove(toolName);
        return true;
    }

    /**
     * 查询工具元数据
     */
    public ToolMeta getToolMeta(String toolName) {
        return toolMap.get(toolName);
    }

    /**
     * 列出所有工具（支持过滤）
     */
    public List<ToolMeta> listTools(String filter) {
        List<ToolMeta> tools = new ArrayList<>(toolMap.values());
        if (filter == null || filter.isEmpty()) {
            return tools;
        }
        // 按类型过滤（BUILT_IN/THIRD_PARTY）
        return tools.stream()
                .filter(tool -> filter.equals(tool.getType()))
                .collect(Collectors.toList());
    }

    /**
     * 检查工具是否为内置
     */
    public boolean isBuiltInTool(String toolName) {
        ToolMeta meta = toolMap.get(toolName);
        return meta != null && "BUILT_IN".equals(meta.getType());
    }
}