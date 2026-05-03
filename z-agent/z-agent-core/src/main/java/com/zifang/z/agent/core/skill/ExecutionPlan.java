package com.zifang.z.agent.core.skill;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ExecutionPlan {

    private String skill;
    private Map<String, Object> params;
    private List<SelectedSkill> decompose;
}
