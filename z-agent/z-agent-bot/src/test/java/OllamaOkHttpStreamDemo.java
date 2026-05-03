import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.*;

/**
 * OkHttp 调用本地 Ollama 流式对话
 * 无任何框架，纯原生，JDK17 完美运行
 */
public class OllamaOkHttpStreamDemo {

    // 本地 Ollama 配置
    private static final String OLLAMA_BASE_URL = "http://localhost:11434";
    private static final String MODEL = "qwen2.5:7b-instruct"; // 你本地的模型 llama3.2, gemma, qwen 都行

    // claude --model qwen2.5:7b-instruct
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .build();

    public static void main(String[] args) {
        String userQuestion = "用最简单的话解释什么是Agent";
        chatStream(userQuestion);
    }

    /**
     * 流式对话
     */
    public static void chatStream(String userMessage) {
        System.out.println("用户: " + userMessage);
        System.out.print("AI: ");

        // 1. 构造请求体
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", userMessage);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL);
        requestBody.put("messages", Collections.singletonList(message));
        requestBody.put("stream", true); // 关键：开启流式输出

        String json = new com.alibaba.fastjson2.JSONObject(requestBody).toString();

        // 2. 构建请求
        Request request = new Request.Builder()
                .url(OLLAMA_BASE_URL + "/api/chat")
                .post(RequestBody.create(json, MediaType.parse("application/json")))
                .build();

        // 3. 异步流式调用
        HTTP_CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody body = response.body()) {
                    if (body == null) return;

                    // 逐行读取流式返回
                    try (Scanner scanner = new Scanner(body.byteStream())) {
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            if (line.isEmpty()) continue;

                            // 解析 Ollama 流式返回格式
                            com.alibaba.fastjson2.JSONObject jsonObject =
                                    com.alibaba.fastjson2.JSON.parseObject(line);

                            // 提取内容
                            if (jsonObject.containsKey("message")) {
                                String content = jsonObject.getJSONObject("message")
                                        .getString("content");
                                System.out.print(content); // 打字机输出
                            }

                            // 结束标志
                            if (jsonObject.getBooleanValue("done")) {
                                System.out.println("\n✅ 流式输出完成");
                            }
                        }
                    }
                }
            }
        });
    }
}