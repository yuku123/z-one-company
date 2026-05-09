package com.zifang.z.agent.mcp.starter;

import com.zifang.z.agent.mcp.core.McpRegistry;
import com.zifang.z.agent.mcp.core.ToolMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 启动时注册所有内置工具到 McpRegistry
 */
@Component
public class BuiltInToolInitializer implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(BuiltInToolInitializer.class);

    @Autowired
    private McpRegistry registry;

    @Override
    public void afterPropertiesSet() {
        logger.info("Registering built-in tools...");

        // echo - 回显输入
        ToolMeta echo = new ToolMeta();
        echo.setToolName("echo");
        echo.setType("BUILT_IN");
        echo.setDescription("Echo back the input message with metadata. Accepts: {message: string}");
        echo.setInputSchema(buildInputSchema(
                "message", "string", "The message to echo back"
        ));
        registry.registerTool(echo);

        // get_time - 获取当前时间
        ToolMeta getTime = new ToolMeta();
        getTime.setToolName("get_time");
        getTime.setType("BUILT_IN");
        getTime.setDescription("Get current system time. Accepts: {timezone: string, optional, default 'Asia/Shanghai'}");
        getTime.setInputSchema(buildInputSchema(
                "timezone", "string", "Timezone string, e.g. 'Asia/Shanghai', 'UTC'"
        ));
        registry.registerTool(getTime);

        // system_info - 系统信息
        ToolMeta sysInfo = new ToolMeta();
        sysInfo.setToolName("system_info");
        sysInfo.setType("BUILT_IN");
        sysInfo.setDescription("Get system information (Java version, OS, memory, etc.)");
        sysInfo.setInputSchema(buildInputSchema(null, null, null)); // no required params
        registry.registerTool(sysInfo);

        // list_modules - 列出项目模块
        ToolMeta listMod = new ToolMeta();
        listMod.setToolName("list_modules");
        listMod.setType("BUILT_IN");
        listMod.setDescription("List all z-one-company modules with descriptions and ports");
        listMod.setInputSchema(buildInputSchema(null, null, null));
        registry.registerTool(listMod);

        // generate_uuid - 生成UUID
        ToolMeta uuid = new ToolMeta();
        uuid.setToolName("generate_uuid");
        uuid.setType("BUILT_IN");
        uuid.setDescription("Generate random UUID(s). Accepts: {count: integer, optional, 1-10, default 1}");
        uuid.setInputSchema(buildInputSchema(
                "count", "integer", "Number of UUIDs to generate (1-10, default 1)"
        ));
        registry.registerTool(uuid);

        logger.info("Registered {} built-in tools", registry.listTools("BUILT_IN").size());
    }

    private Map<String, Object> buildInputSchema(String paramName, String paramType, String paramDesc) {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");

        Map<String, Object> properties = new HashMap<>();
        if (paramName != null) {
            Map<String, Object> prop = new HashMap<>();
            prop.put("type", paramType);
            prop.put("description", paramDesc);
            properties.put(paramName, prop);
        }
        schema.put("properties", properties);

        // required 列表
        schema.put("required", new ArrayList<String>());

        return schema;
    }
}
