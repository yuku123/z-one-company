package com.zifang.z.agent.core.skill;

import lombok.Data;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class Skill {
    private String name;
    private String description;
    private Map<String, Object> parameters;
    private List<String> required;

    // 关键：加注解映射 JSON 里的 is_composite 字段
    @JsonProperty("is_composite")
    private boolean isComposite;
}