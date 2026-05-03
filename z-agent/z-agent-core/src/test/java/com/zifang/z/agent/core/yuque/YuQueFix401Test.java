package com.zifang.z.agent.core.yuque;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class YuQueFix401Test {
    public static void main(String[] args) throws UnsupportedEncodingException {
        // 1. 替换为新生成的全权限 Token
        String token = "MHKmrZBO2e7KFh9gHU2XNmXRs5wjpJ7P4BWPJfQ9";
        String username = "yuku123";
        String repoName = "tcafwh";

        // 2. 手动编码中文知识库名称
        String encodedRepoName = URLEncoder.encode(repoName, String.valueOf(StandardCharsets.UTF_8));
        String url = "https://www.yuque.com/api/v2/repos/" + username + "/" + encodedRepoName;

        // 3. 构建请求
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .build();

        // 4. 执行请求并打印结果
        try {
            Response response = client.newCall(request).execute();
            System.out.println("响应码：" + response.code());
            System.out.println("响应体：" + (response.body() != null ? response.body().string() : "无"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}