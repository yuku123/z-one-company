package com.zifang.z.agent.core.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LlmCallerConfig {

    private String modelName;

    @Builder.Default
    private String baseUrl = "http://localhost:11434";
}
