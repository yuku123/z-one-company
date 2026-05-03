package com.zifang.z.agent.core.tool;

import java.util.Map;

@FunctionalInterface
public interface ToolExecutor {
    String execute(Map<String, Object> args);
}