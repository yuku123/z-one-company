package com.zifang.z.agent.core.skill;

import lombok.Data;

import java.util.Map;

@Data
public class SelectedSkill {
    private String skill;
    private Map<String, Object> params;
}
