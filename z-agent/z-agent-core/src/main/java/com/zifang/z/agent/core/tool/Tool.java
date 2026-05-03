package com.zifang.z.agent.core.tool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tool {
    private String name;                // 工具名
    private String desc;                // 工具描述
    private List<ToolParam> params;     // 参数列表

    // 生成工具描述文本（Java8 Lambda）
    public String toDesc() {
        StringBuilder sb = new StringBuilder();
        sb.append("- ").append(name).append("：").append(desc).append("\n");
        if (params != null && !params.isEmpty()) {
            sb.append("  参数：\n");
            for (ToolParam p : params) {
                sb.append("    - ").append(p.getName()).append("（")
                        .append(p.getType()).append(p.isRequired() ? ", 必填" : "").append("）：")
                        .append(p.getDesc()).append("\n");
            }
        }
        return sb.toString();
    }
}

