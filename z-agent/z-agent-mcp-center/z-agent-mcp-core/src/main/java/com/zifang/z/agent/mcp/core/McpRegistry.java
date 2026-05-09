package com.zifang.z.agent.mcp.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * MCP 工具注册中心（线程安全，纯 Java，无 Spring 依赖）
 * 
 * 可被 Spring 容器管理（@Component），也可独立使用。
 */
public class McpRegistry {

    private final Map<String, ToolMeta> toolMap = new ConcurrentHashMap<>();

    /**
     * 注册工具
     */
    public boolean registerTool(ToolMeta toolMeta) {
        if (toolMeta == null || toolMeta.getToolName() == null) return false;
        if (toolMap.containsKey(toolMeta.getToolName())) return false;
        toolMeta.setCreateTime(System.currentTimeMillis());
        toolMap.put(toolMeta.getToolName(), toolMeta);
        return true;
    }

    /**
     * 覆盖注册（存在则更新）
     */
    public boolean registerOrUpdate(ToolMeta toolMeta) {
        if (toolMeta == null || toolMeta.getToolName() == null) return false;
        toolMeta.setCreateTime(System.currentTimeMillis());
        toolMap.put(toolMeta.getToolName(), toolMeta);
        return true;
    }

    /**
     * 注销工具（仅第三方）
     */
    public boolean unregisterTool(String toolName) {
        ToolMeta meta = toolMap.get(toolName);
        if (meta == null || "BUILT_IN".equals(meta.getType())) return false;
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
        if (filter == null || filter.isEmpty()) return tools;
        return tools.stream()
                .filter(t -> filter.equals(t.getType()))
                .collect(Collectors.toList());
    }

    /**
     * 检查是否为内置工具
     */
    public boolean isBuiltInTool(String toolName) {
        ToolMeta meta = toolMap.get(toolName);
        return meta != null && "BUILT_IN".equals(meta.getType());
    }

    /**
     * 清空所有工具
     */
    public void clear() {
        toolMap.clear();
    }

    public int size() {
        return toolMap.size();
    }
}
