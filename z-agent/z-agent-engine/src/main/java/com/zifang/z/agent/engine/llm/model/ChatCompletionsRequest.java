package com.zifang.z.agent.engine.llm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Request payload for the Chat Completions HTTP API.
 * Compatible with OpenAI's Chat Completions API format.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatCompletionsRequest {

    private String model;
    private List<ChatMessage> messages;
    private Boolean stream;
    private List<OpenAITool> tools;
    private Map<String, Object> extraParams;

    public ChatCompletionsRequest() {}

    public ChatCompletionsRequest(String model, List<ChatMessage> messages) {
        this.model = model;
        this.messages = messages;
    }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public List<ChatMessage> getMessages() { return messages; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }
    public Boolean getStream() { return stream; }
    public void setStream(Boolean stream) { this.stream = stream; }
    public List<OpenAITool> getTools() { return tools; }
    public void setTools(List<OpenAITool> tools) { this.tools = tools; }
    public Map<String, Object> getExtraParams() { return extraParams; }
    public void setExtraParams(Map<String, Object> extraParams) { this.extraParams = extraParams; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String model;
        private List<ChatMessage> messages;
        private Boolean stream;
        private List<OpenAITool> tools;

        public Builder model(String model) { this.model = model; return this; }
        public Builder messages(List<ChatMessage> messages) { this.messages = messages; return this; }
        public Builder stream(Boolean stream) { this.stream = stream; return this; }
        public Builder tools(List<OpenAITool> tools) { this.tools = tools; return this; }

        public ChatCompletionsRequest build() {
            ChatCompletionsRequest req = new ChatCompletionsRequest();
            req.model = this.model;
            req.messages = this.messages;
            req.stream = this.stream;
            req.tools = this.tools;
            return req;
        }
    }
}
