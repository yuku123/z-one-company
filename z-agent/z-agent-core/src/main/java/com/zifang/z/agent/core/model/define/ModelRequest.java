package com.zifang.z.agent.core.model.define;

import com.zifang.z.agent.core.tool.ToolSchema;
import lombok.Data;

import java.util.List;

@Data
public class ModelRequest {

    private List<ModelMessage> messages;

    private List<ToolSchema> tools;

    private ModelOptions options;

}
