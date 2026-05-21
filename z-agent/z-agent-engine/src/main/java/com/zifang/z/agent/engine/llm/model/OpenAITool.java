package com.zifang.z.agent.engine.llm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * OpenAI tool definition for Chat Completions API.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenAITool {

    @JsonProperty("type")
    private String type = "function";

    @JsonProperty("function")
    private OpenAIToolFunction function;

    public OpenAITool() {}

    public OpenAITool(OpenAIToolFunction function) {
        this.function = function;
    }

    public static OpenAITool of(String name, String description, String parametersJson) {
        OpenAIToolFunction f = new OpenAIToolFunction(name, description, parametersJson);
        return new OpenAITool(f);
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public OpenAIToolFunction getFunction() { return function; }
    public void setFunction(OpenAIToolFunction function) { this.function = function; }

    public static class OpenAIToolFunction {
        private String name;
        private String description;
        private String parameters; // JSON Schema string

        public OpenAIToolFunction() {}

        public OpenAIToolFunction(String name, String description, String parameters) {
            this.name = name;
            this.description = description;
            this.parameters = parameters;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getParameters() { return parameters; }
        public void setParameters(String parameters) { this.parameters = parameters; }
    }
}
