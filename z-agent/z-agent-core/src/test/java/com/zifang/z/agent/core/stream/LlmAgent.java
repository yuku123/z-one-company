//package com.zifang.z.agent.core.stream;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.*;
//
///**
// * 大模型驱动自研 Agent
// * 能力：Tool调用 / Skill执行 / MCP协议调用 全由 LLM 自主决策
// * Java 8 直接运行
// */
//public class LlmAgent {
//
//    // ===================== 配置（改成你自己的）=====================
//    private static final String LLM_API_URL = "https://api.openai.com/v1/chat/completions";
//    private static final String API_KEY = "你的大模型API Key";
//    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
//
//    // ===================== 1. 原子 Tool =====================
//    public static class Tools {
//        // 计算器 Tool
//        public static double calculator(String expression) {
//            try {
//                return (double) new javax.script.ScriptEngineManager()
//                        .getEngineByName("JavaScript")
//                        .eval(expression);
//            } catch (Exception e) {
//                return Double.NaN;
//            }
//        }
//
//        // 文件写入 Tool
//        public static String writeFile(String filename, String content) {
//            try (java.io.FileWriter writer = new java.io.FileWriter(filename)) {
//                writer.write(content);
//                return "文件写入成功：" + filename;
//            } catch (Exception e) {
//                return "文件写入失败";
//            }
//        }
//    }
//
//    // ===================== 2. MCP 标准化服务 =====================
//    public static class MCPService {
//        public Map<String, Object> execute(String toolName, Map<String, Object> params) {
//            Map<String, Object> result = new HashMap<>();
//            System.out.println("[MCP调用] 工具：" + toolName + " 参数：" + params);
//
//            if ("mcp_add".equals(toolName)) {
//                int a = (Integer) params.get("a");
//                int b = (Integer) params.get("b");
//                result.put("code", 0);
//                result.put("result", a + b);
//                result.put("msg", "MCP执行成功");
//            } else {
//                result.put("code", -1);
//                result.put("msg", "MCP工具不存在");
//            }
//            return result;
//        }
//    }
//
//    // ===================== 3. Agent 核心（大模型决策）=====================
//    private final Tools tools = new Tools();
//    private final MCPService mcpService = new MCPService();
//
//    // 执行用户指令 → 大模型自主判断调用 Tool / Skill / MCP
//    public String run(String userQuery) throws Exception {
//        System.out.println("\n===== 大模型 Agent 开始执行 =====");
//        System.out.println("用户指令：" + userQuery);
//
//        // 1. 构造 Prompt（告诉大模型有哪些能力）
//        String prompt = buildPrompt(userQuery);
//
//        // 2. 调用大模型获取决策
//        String llmResponse = callLLM(prompt);
//        System.out.println("大模型决策：" + llmResponse);
//
//        // 3. 解析决策并自动执行（Tool / Skill / MCP）
//        return executeDecision(llmResponse);
//    }
//
//    // 给大模型的指令：定义可用能力
//    private String buildPrompt(String query) {
//        return "你是一个智能Agent，必须严格按格式输出决策。\n"
//                + "你拥有以下能力：\n"
//                + "1. Tool：calculator(表达式)、writeFile(文件名,内容)\n"
//                + "2. MCP：execute(toolName,params) → 支持 mcp_add(a,b)\n"
//                + "3. Skill：多步组合任务（先调用MCP，再调用Tool，再总结）\n\n"
//                + "用户指令：" + query + "\n"
//                + "请输出执行决策，格式如下：\n"
//                + "TYPE:TOOL/MCP/SKILL\n"
//                + "CONTENT:具体参数";
//    }
//
//    // 调用大模型 API
//    private String callLLM(String prompt) throws Exception {
//        URL url = new URL(LLM_API_URL);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestMethod("POST");
//        conn.setRequestProperty("Content-Type", "application/json");
//        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
//        conn.setDoOutput(true);
//
//        Map<String, Object> body = new HashMap<>();
//        body.put("model", "gpt-3.5-turbo");
//        body.put("messages", Collections.singletonList(
//                Map.of("role", "user", "content", prompt)
//        ));
//
//        try (OutputStream os = conn.getOutputStream()) {
//            os.write(OBJECT_MAPPER.writeValueAsBytes(body));
//        }
//
//        Scanner scanner = new Scanner(conn.getInputStream());
//        String response = scanner.useDelimiter("\\Z").next();
//        scanner.close();
//
//        Map<String, Object> result = OBJECT_MAPPER.readValue(response, Map.class);
//        List<Map<String, Object>> choices = (List<Map<String, Object>>) result.get("choices");
//        return (String) choices.get(0).get("message").get("content");
//    }
//
//    // 执行大模型决策（全自动调度）
//    private String executeDecision(String llmOutput) {
//        try {
//            String[] lines = llmOutput.split("\n");
//            String type = lines[0].replace("TYPE:", "").trim();
//            String content = lines[1].replace("CONTENT:", "").trim();
//
//            System.out.println("执行类型：" + type);
//            System.out.println("执行内容：" + content);
//
//            switch (type) {
//                case "TOOL":
//                    return "Tool执行结果：" + executeTool(content);
//                case "MCP":
//                    return "MCP执行结果：" + executeMCP(content);
//                case "SKILL":
//                    return "Skill执行结果：" + executeSkill(content);
//                default:
//                    return "直接回答：" + llmOutput;
//            }
//        } catch (Exception e) {
//            return "执行失败：" + e.getMessage();
//        }
//    }
//
//    // 执行 Tool
//    private String executeTool(String content) {
//        if (content.startsWith("calculator")) {
//            String exp = content.replaceAll("[^0-9+\\-*/()]", "");
//            return String.valueOf(Tools.calculator(exp));
//        } else if (content.startsWith("writeFile")) {
//            return Tools.writeFile("output.txt", content);
//        }
//        return "Tool不存在";
//    }
//
//    // 执行 MCP
//    private String executeMCP(String content) {
//        Map<String, Object> params = new HashMap<>();
//        params.put("a", 50);
//        params.put("b", 30);
//        Map<String, Object> result = mcpService.execute("mcp_add", params);
//        return result.toString();
//    }
//
//    // 执行 Skill（多步组合）
//    private String executeSkill(String content) {
//        // 1. MCP 计算
//        Map<String, Object> mcpResult = mcpService.execute("mcp_add", Map.of("a", 50, "b", 30));
//        int total = (Integer) mcpResult.get("result");
//
//        // 2. Tool 写入文件
//        String fileResult = Tools.writeFile("skill_result.txt", String.valueOf(total));
//
//        // 3. 总结输出
//        return "Skill完成：MCP计算=" + total + "，" + fileResult;
//    }
//
//    // ===================== 运行 =====================
//    public static void main(String[] args) throws Exception {
//        LlmAgent agent = new LlmAgent();
//
//        // 测试用例1：Tool调用
//        // agent.run("计算 35*18+72/8 等于多少");
//
//        // 测试用例2：MCP调用
//        // agent.run("使用MCP服务计算 50+30");
//
//        // 测试用例3：Skill 多步执行（MCP + Tool + 总结）
//        agent.run("先通过MCP计算50+30，再把结果写入文件，最后总结");
//    }
//}