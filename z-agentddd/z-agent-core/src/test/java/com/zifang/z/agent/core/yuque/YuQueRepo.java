package com.zifang.z.agent.core.yuque;
import lombok.Data;

/**
 * 语雀空间/知识库基础信息
 */
@Data
public class YuQueRepo {
    private Long id;
    private String name; // 知识库名称
    private String namespace; // 知识库命名空间
    private String description;
    private Long user_id; // 创建者ID
}