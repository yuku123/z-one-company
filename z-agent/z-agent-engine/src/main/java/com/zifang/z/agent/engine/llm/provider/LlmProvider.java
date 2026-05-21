package com.zifang.z.agent.engine.llm.provider;

import com.zifang.z.agent.engine.llm.model.ChatCompletionsRequest;
import com.zifang.z.agent.engine.llm.model.ChatCompletionsResponse;
import com.zifang.z.agent.engine.llm.model.ChatMessage;
import java.io.IOException;
import java.util.List;

/**
 * LLM Provider interface for different model providers.
 */
public interface LlmProvider {

    /**
     * Get the provider code (ollama, openai, azure, etc.)
     */
    String getProviderCode();

    /**
     * Get the default base URL for this provider.
     */
    String getDefaultBaseUrl();

    /**
     * Chat completions API.
     */
    ChatCompletionsResponse chat(ChatCompletionsRequest request) throws IOException;

    /**
     * Build a chat request with messages.
     */
    default ChatCompletionsRequest buildRequest(String model, List<ChatMessage> messages) {
        return ChatCompletionsRequest.builder()
                .model(model)
                .messages(messages)
                .build();
    }

    /**
     * Check if the provider is available.
     */
    default boolean isAvailable() {
        return true;
    }
}
