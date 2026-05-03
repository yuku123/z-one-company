package com.zifang.z.agent.core.yuque;

import lombok.Data;

/**
 * 语雀文档实体
 */
@Data
public class YuQueDoc {
    // 文档ID
    private Long id;
    // 文档标题
    private String title;
    // 文档简介
    private String description;
    // 文档路径标识
    private String slug;
    // 所属知识库信息
    private Book book;

    /**
     * 获取文档完整访问地址
     *
     * @param username 语雀用户名
     * @return 完整URL
     */
    public String getFullUrl(String username) {
        return String.format("https://www.yuque.com/%s/%s/%s",
                username, book.getNamespace(), slug);
    }

    @Data
    public static class Book {
        // 知识库命名空间（地址中的标识）
        private String namespace;
        // 知识库名称
        private String name;
    }
}
