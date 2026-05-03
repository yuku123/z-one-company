import com.zifang.z.agent.core.model.LlmCallerConfig;
import com.zifang.z.agent.core.model.LlmCallerFactory;
import com.zifang.z.agent.core.model.define.Model;
import com.zifang.z.agent.core.model.define.ModelMessage;
import com.zifang.z.agent.core.tool.Tool;
import com.zifang.z.agent.core.tool.ToolCall;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.zifang.z.agent.core.tool.ToolManager.*;

public class ZBotFinal {

    public static void main(String[] args) {

        Model ai = LlmCallerFactory.create(
                LlmCallerFactory.LLM_CALLER_TYPE_OLLAMA,
                LlmCallerConfig.builder().modelName("qwen3:8b").build()
        );

        // 2. 初始化记忆（仅存user/assistant/system）
        List<ModelMessage> memory = new ArrayList<>();

        // 3. 系统提示（标准Tool Calling引导）
        StringBuilder toolPrompt = new StringBuilder();
        toolPrompt.append("你是标准Tool Calling助手，支持以下工具：\n");
        for (Tool tool : TOOLS) {
            toolPrompt.append(tool.toDesc());
        }
        toolPrompt.append("规则：\n");
        toolPrompt.append("1. 调用工具必须返回JSON：{\"toolName\":\"工具名\",\"args\":{\"参数\":\"值\"}}\n");
        toolPrompt.append("2. 不调用工具时直接返回自然语言回答");
        memory.add(ModelMessage.of("system", toolPrompt.toString()));

        // 4. 交互式对话
        Scanner scanner = new Scanner(System.in);
        System.out.println("===== OpenClaw 小龙虾助手（Java8+Ollama）=====");
        System.out.println("支持：计算100+200*3 | 查时间 | 北京天气 | exit退出\n");

        while (true) {
            System.out.print("你：");
            String input = scanner.nextLine().trim();

            if ("exit".equalsIgnoreCase(input)) {
                System.out.println("Bye~");
                scanner.close();
                break;
            }
            if (StringUtils.isEmpty(input)) continue;

            try {
                // Step1: 用户输入入记忆
                memory.add(ModelMessage.of("user", input));
                System.out.println("AI：思考中...");

                // Step2: 调用模型判断工具
                String modelResp = ai.chat(new ArrayList<>(memory));
                System.out.println("AI：[原始响应] " + modelResp + "\n");

                // Step3: 工具调用判断
                String finalAnswer;
                if (modelResp.contains("\"toolName\":\"") && modelResp.contains("\"args\":")) {
                    // 解析并执行工具
                    ToolCall call = parseToolCall(modelResp);
                    System.out.println("AI：[调用工具] " + call.getToolName() + " | 参数：" + call.getArgs());
                    String toolResult = execToolCall(call);
                    System.out.println("AI：[工具结果] " + toolResult + "\n");

                    // 工具结果回传模型
                    memory.add(ModelMessage.of("tool", toolResult));
                    finalAnswer = ai.chat(new ArrayList<>(memory));
                } else {
                    // 无需工具，直接回答
                    finalAnswer = modelResp;
                }

                // Step4: 存储回答，完成闭环
                memory.add(ModelMessage.of("assistant", finalAnswer));
                System.out.println("AI：" + finalAnswer + "\n");

            } catch (Exception e) {
                System.err.println("AI：处理失败：" + e.getMessage() + "\n");
            }
        }
    }
}