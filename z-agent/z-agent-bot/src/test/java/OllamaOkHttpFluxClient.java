import okhttp3.*;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.util.*;

public class OllamaOkHttpFluxClient {

    private static final String OLLAMA_HOST = "http://localhost:11434";
    private static final String MODEL = "qwen2.5:7b-instruct"; // 你本地的模型 llama3.2, gemma, qwen 都行
    private static final OkHttpClient OK_HTTP = new OkHttpClient();

    /**
     * 流式对话 → 返回 Flux<String>
     */
    public Flux<String> chatStream(String userMessage) {
        // 1. 创建 Sinks，安全发射流式数据
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();

        // 2. 构建请求体
        Map<String, Object> msg = new HashMap<>();
        msg.put("role", "user");
        msg.put("content", userMessage);

        Map<String, Object> body = new HashMap<>();
        body.put("model", MODEL);
        body.put("messages", Collections.singletonList(msg));
        body.put("stream", true);

        String json = JSON.toJSONString(body);

        Request request = new Request.Builder()
                .url(OLLAMA_HOST + "/api/chat")
                .post(RequestBody.create(json, MediaType.parse("application/json")))
                .build();

        // 3. OkHttp 异步调用
        OK_HTTP.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                sink.tryEmitError(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody body = response.body()) {
                    if (body == null) {
                        sink.tryEmitComplete();
                        return;
                    }

                    try (Scanner scanner = new Scanner(body.byteStream())) {
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine().trim();
                            if (line.isEmpty()) continue;

                            JSONObject obj = JSON.parseObject(line);
                            if (obj.containsKey("message")) {
                                String content = obj.getJSONObject("message").getString("content");
                                if (content != null && !content.isEmpty()) {
                                    sink.tryEmitNext(content); // 发射内容
                                }
                            }

                            // 结束
                            if (obj.getBooleanValue("done")) {
                                sink.tryEmitComplete();
                            }
                        }
                    }
                } catch (Exception e) {
                    sink.tryEmitError(e);
                }
            }
        });

        return sink.asFlux();
    }

    public static void main(String[] args) {
        OllamaOkHttpFluxClient client = new OllamaOkHttpFluxClient();

        System.out.print("AI: ");
        client.chatStream("什么是Agent？")
                .doOnNext(content -> {
                    System.out.print(content); // 逐字输出
                })
                .doOnComplete(() -> {
                    System.out.println("\n✅ 完成");
                })
                .subscribe();
    }
}