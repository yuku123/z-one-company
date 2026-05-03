package com.zifang.z.agent.core.tool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToolParam {
    private String name;        // 参数名
    private String type;        // string/number/boolean
    private boolean required;   // 是否必填
    private String desc;        // 参数描述
}