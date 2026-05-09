package com.zifang.z.agent.mcp.starter;

import com.zifang.z.agent.mcp.core.ToolExecutor;
import com.zifang.z.agent.mcp.core.McpRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 基于 @McpTool 注解方法的工具执行器。
 * 持有 bean → method 的映射，在 tools/call 时反射调用。
 */
public class McpAnnotationToolExecutor implements ToolExecutor {

    private final Map<String, ToolInvoker> invokers = new LinkedHashMap<>();
    private final McpRegistry registry;

    public McpAnnotationToolExecutor(McpRegistry registry) {
        this.registry = registry;
    }

    /**
     * 注册一个工具方法
     */
    public void register(String toolName, Object bean, Method method) {
        invokers.put(toolName, new ToolInvoker(bean, method));
    }

    @Override
    public boolean supports(String toolName) {
        return invokers.containsKey(toolName);
    }

    @Override
    public Map<String, Object> execute(String toolName, Map<String, Object> arguments) {
        ToolInvoker invoker = invokers.get(toolName);
        if (invoker == null) {
            return buildError("Tool not found: " + toolName);
        }

        if (arguments == null) {
            arguments = Collections.emptyMap();
        }

        try {
            // 构建参数数组
            Method method = invoker.method;
            java.lang.reflect.Parameter[] params = method.getParameters();
            Object[] args = new Object[params.length];

            for (int i = 0; i < params.length; i++) {
                McpParam paramAnno = params[i].getAnnotation(McpParam.class);
                String paramName;
                if (paramAnno != null && !paramAnno.name().isEmpty()) {
                    paramName = paramAnno.name();
                } else {
                    paramName = params[i].getName();
                    // Java 8 无 -parameters 时回退
                    if (paramName.startsWith("arg")) {
                        paramName = "param" + i;
                    }
                }

                Object rawValue = arguments.get(paramName);
                args[i] = convertValue(rawValue, params[i].getType());
            }

            Object result = method.invoke(invoker.bean, args);

            // 构建 MCP 标准返回
            return buildTextResult(result != null ? result.toString() : "null");

        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            return buildError("Tool '" + toolName + "' failed: " + cause.getMessage());
        } catch (Exception e) {
            return buildError("Tool '" + toolName + "' error: " + e.getMessage());
        }
    }

    /**
     * 类型转换
     */
    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType.isInstance(value)) return value;

        String str = value.toString();
        if (targetType == String.class) return str;
        if (targetType == int.class || targetType == Integer.class) return Integer.parseInt(str);
        if (targetType == long.class || targetType == Long.class) return Long.parseLong(str);
        if (targetType == double.class || targetType == Double.class) return Double.parseDouble(str);
        if (targetType == boolean.class || targetType == Boolean.class) return Boolean.parseBoolean(str);

        return value;
    }

    // ── helpers ──

    private Map<String, Object> buildTextResult(String text) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> content = new ArrayList<>();
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("type", "text");
        item.put("text", text);
        content.add(item);
        result.put("content", content);
        result.put("isError", false);
        return result;
    }

    private Map<String, Object> buildError(String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> content = new ArrayList<>();
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("type", "text");
        item.put("text", "Error: " + message);
        content.add(item);
        result.put("content", content);
        result.put("isError", true);
        return result;
    }

    // ── inner class ──

    private static class ToolInvoker {
        final Object bean;
        final Method method;

        ToolInvoker(Object bean, Method method) {
            this.bean = bean;
            this.method = method;
        }
    }
}
