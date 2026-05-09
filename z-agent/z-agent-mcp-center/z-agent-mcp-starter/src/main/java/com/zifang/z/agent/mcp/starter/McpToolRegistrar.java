package com.zifang.z.agent.mcp.starter;

import com.zifang.z.agent.mcp.core.McpRegistry;
import com.zifang.z.agent.mcp.core.ToolMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Bean 后处理器：扫描所有 Bean 中标注 @McpTool 的方法，
 * 自动注册到 McpRegistry。
 */
public class McpToolRegistrar implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(McpToolRegistrar.class);

    private final McpRegistry registry;
    private final McpAnnotationToolExecutor executor;

    public McpToolRegistrar(McpRegistry registry, McpAnnotationToolExecutor executor) {
        this.registry = registry;
        this.executor = executor;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Class<?> clazz = bean.getClass();
        // 避免代理类重复扫描
        if (clazz.getName().contains("$$")) return bean;

        for (Method method : clazz.getDeclaredMethods()) {
            McpTool annotation = method.getAnnotation(McpTool.class);
            if (annotation == null) continue;

            String toolName = annotation.name().isEmpty() ? method.getName() : annotation.name();
            String description = annotation.description().isEmpty()
                    ? "Auto-registered tool: " + toolName
                    : annotation.description();

            Map<String, Object> inputSchema = buildInputSchema(method);

            ToolMeta meta = new ToolMeta();
            meta.setToolName(toolName);
            meta.setType("BUILT_IN");
            meta.setDescription(description);
            meta.setInputSchema(inputSchema);

            registry.registerOrUpdate(meta);

            // 同步注册到 executor，使 tools/call 可执行
            executor.register(toolName, bean, method);

            log.info("Registered MCP tool: {} (bean={}, method={})", toolName, beanName, method.getName());
        }

        return bean;
    }

    /**
     * 从方法参数构建 JSON Schema
     */
    private Map<String, Object> buildInputSchema(Method method) {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");

        Map<String, Object> properties = new LinkedHashMap<>();
        List<String> required = new ArrayList<>();

        Parameter[] params = method.getParameters();
        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];
            McpParam paramAnno = param.getAnnotation(McpParam.class);

            String paramName;
            String paramDesc = "";
            boolean isRequired = false;

            if (paramAnno != null) {
                paramName = paramAnno.name().isEmpty() ? param.getName() : paramAnno.name();
                paramDesc = paramAnno.description();
                isRequired = paramAnno.required();
            } else {
                paramName = param.getName();
            }

            // 处理参数名在 Java 8 下为 arg0, arg1 的问题
            if (paramName.startsWith("arg") && paramName.length() <= 5) {
                paramName = "param" + i;
            }

            Map<String, Object> prop = new LinkedHashMap<>();
            prop.put("type", javaTypeToJsonType(param.getType()));
            if (!paramDesc.isEmpty()) {
                prop.put("description", paramDesc);
            }
            properties.put(paramName, prop);

            if (isRequired) {
                required.add(paramName);
            }
        }

        schema.put("properties", properties);
        schema.put("required", required);
        return schema;
    }

    /**
     * Java 类型 → JSON Schema 类型
     */
    static String javaTypeToJsonType(Class<?> type) {
        if (type == String.class || type == Character.class || type == char.class) return "string";
        if (type == Integer.class || type == int.class || type == Long.class || type == long.class
                || type == Short.class || type == short.class || type == Byte.class || type == byte.class)
            return "integer";
        if (type == Double.class || type == double.class || type == Float.class || type == float.class)
            return "number";
        if (type == Boolean.class || type == boolean.class) return "boolean";
        if (type.isArray() || Collection.class.isAssignableFrom(type)) return "array";
        return "object";
    }
}
