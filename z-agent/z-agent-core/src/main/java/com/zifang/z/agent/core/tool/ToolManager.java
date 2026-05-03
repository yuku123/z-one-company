package com.zifang.z.agent.core.tool;

import org.apache.commons.lang3.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.time.LocalDateTime;
import java.util.*;

public class ToolManager {

    // 全局工具定义
    public static final List<Tool> TOOLS = new ArrayList<>();
    public static final Map<String, ToolExecutor> TOOL_EXEC = new HashMap<>();

    // 初始化工具（静态代码块，Java8兼容）
    static {
        // 1. 计算工具
        Tool calcTool = new Tool(
                "calculate",
                "数学计算，支持加减乘除",
                Collections.singletonList(new ToolParam("exp", "string", true, "数学表达式，如100+200*3"))
        );
        TOOLS.add(calcTool);
        TOOL_EXEC.put("calculate", args -> {
            String exp = args.get("exp").toString();
            try {
                ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
                return "计算结果：" + engine.eval(exp);
            } catch (ScriptException e) {
                return "计算失败：" + e.getMessage();
            }
        });

        // 2. 时间工具
        Tool timeTool = new Tool(
                "get_time",
                "查询当前系统时间",
                Collections.emptyList()
        );
        TOOLS.add(timeTool);
        TOOL_EXEC.put("get_time", args -> "当前时间：" + LocalDateTime.now());

        // 3. 天气工具
        Tool weatherTool = new Tool(
                "get_weather",
                "查询城市当日天气",
                Collections.singletonList(new ToolParam("city", "string", true, "城市名，如北京"))
        );
        TOOLS.add(weatherTool);
        TOOL_EXEC.put("get_weather", args -> {
            String city = args.get("city").toString();
            Map<String, String> weather = new HashMap<>();
            weather.put("北京", "晴，20℃");
            weather.put("上海", "多云，22℃");
            return "天气结果：" + weather.getOrDefault(city, "未知城市");
        });
    }


    /**
     * 解析模型返回的ToolCall指令（兼容Java8）
     */
    public static ToolCall parseToolCall(String resp) {
        ToolCall call = new ToolCall();
        if (!resp.contains("\"toolName\":\"") || !resp.contains("\"args\":")) {
            return call;
        }

        // 解析工具名
        String toolName = resp.split("\"toolName\":\"")[1].split("\"")[0];
        call.setToolName(toolName);

        // 解析参数（简单JSON解析，Java8兼容）
        String argsStr = resp.split("\"args\":")[1].split("}")[0] + "}";
        Map<String, Object> args = new HashMap<>();
        argsStr = argsStr.replace("{", "").replace("}", "").replace("\"", "");
        if (StringUtils.isNotEmpty(argsStr)) {
            for (String arg : argsStr.split(",")) {
                String[] parts = arg.split(":", 2);
                if (parts.length == 2) {
                    args.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
        call.setArgs(args);
        return call;
    }

    /**
     * 执行工具调用
     */
    public static String execToolCall(ToolCall call) {
        if (call == null || StringUtils.isEmpty(call.getToolName())) {
            return "无效工具调用";
        }
        ToolExecutor executor = TOOL_EXEC.get(call.getToolName());
        if (executor == null) {
            return "未知工具：" + call.getToolName();
        }
        return executor.execute(call.getArgs() == null ? new HashMap<>() : call.getArgs());
    }

}
