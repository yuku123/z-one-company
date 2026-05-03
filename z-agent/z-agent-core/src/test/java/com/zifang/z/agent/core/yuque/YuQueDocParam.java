package com.zifang.z.agent.core.yuque;

import lombok.Data;

import java.util.Map;

/**
 * 语雀文档操作入参
 */
@Data
public class YuQueDocParam {
    // 文档标题（必填）
    private String title;
    // 文档内容（Markdown格式，必填）
    private String body;
    // 文档描述（可选）
    private String description;
    // 父文档ID（可选，用于归类）
    private Long parent_id;
    // 自定义Slug（可选，文档地址标识）
    private String slug;
    // 是否公开（1=公开，0=私有，默认私有）
    private Integer public_;
    // 其他扩展属性
    private Map<String, Object> extras;
}
