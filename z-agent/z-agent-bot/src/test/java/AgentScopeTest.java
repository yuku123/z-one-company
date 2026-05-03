
import com.zifang.z.agent.core.tool.ToolExecutor;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.message.Msg;
import io.agentscope.core.model.OllamaChatModel;
import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;
import io.agentscope.core.tool.Toolkit;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.List;

/**
 * AgentScope Java + 本地 Ollama 对接 Demo
 * 支持：ReAct + Tool调用 + 现代化Agent行为
 */
public class AgentScopeTest {

    // 本地Ollama配置
    private static final String OLLAMA_HOST = "http://localhost:11434";
    private static final String MODEL_NAME = "qwen2.5"; // 你本地的模型名


    // ====================== 真正能操作电脑的工具类 ======================
    public static class LocalExecuteToolkit {

        /**
         * 本地创建文件并写入内容
         * 严格对齐官方 @Tool 格式：name + description
         * 参数严格使用 @ToolParam
         */
        @Tool(
                name = "create_and_write_file",
                description = "在用户本地计算机上创建文件，并写入文本或代码内容，真实写入磁盘，非模拟"
        )
        public String createAndWriteFile(
                @ToolParam(name = "fileName", description = "要创建的文件名，例如 pt.py") String fileName,
                @ToolParam(name = "content", description = "要写入文件的完整内容，支持代码、文本") String content
        ) {
            try {
                FileWriter writer = new FileWriter(fileName);
                writer.write(content);
                writer.flush();
                writer.close();
                return "✅ 文件创建并写入成功：" + fileName;
            } catch (Exception e) {
                return "❌ 文件创建失败：" + e.getMessage();
            }
        }

        /**
         * 执行本地系统命令
         * 严格对齐官方格式
         */
        @Tool(
                name = "execute_local_command",
                description = "在用户本地操作系统执行命令，如 Python 脚本、Shell/CMD 指令，返回控制台输出"
        )
        public String executeLocalCommand(
                @ToolParam(name = "command", description = "要执行的命令，例如 python3 pt.py") String command
        ) {
            try {
                Process process = Runtime.getRuntime().exec(command);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), "GBK")
                );
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                reader.close();
                process.waitFor();
                return "✅ 命令执行结果：\n" + result.toString().trim();
            } catch (Exception e) {
                return "❌ 命令执行失败：" + e.getMessage();
            }
        }
    }


    @Test
    public  void ddd() {
        // Create and register tools
        Toolkit toolkit = new Toolkit();
        toolkit.registerTool(new LocalExecuteToolkit());


        // 创建智能体并内联配置模型
        ReActAgent agent = ReActAgent.builder()
                .name("Assistant")
                .sysPrompt("你是一个有帮助的 AI 助手。")
                .model(OllamaChatModel.builder()
                        .baseUrl("http://localhost:11434")
                        .modelName("qwen2.5:7b-instruct")
                        .build())
                .toolkit(toolkit)
                .build();

        Msg response = agent.call(Msg.builder()
                .textContent("在我本地制造一个文件， ptd.py , 里面写上一个获取时间的代码， 你执行，将结果给我，注意！直接执行")
                .build()).block();
        System.out.println(response.getTextContent());
    }
}