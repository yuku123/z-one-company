package com.zifang.z.agent.mcp.starter.protocol;

import lombok.Data;

import java.util.Map;

/**
 * MCP Resource 定义（符合 MCP 2024-11-05 协议）
 */
@Data
public class McpResource {

    /**
     * 资源 URI
     */
    private String uri;

    /**
     * 资源名称
     */
    private String name;

    /**
     * 资源描述
     */
    private String description;

    /**
     * MIME 类型
     */
    private String mimeType;

    /**
     * 资源大小（字节）
     */
    private Long size;

    /**
     * 资源模板（用于动态资源）
     */
    private ResourceTemplate template;

    /**
     * 资源注解
     */
    private Map<String, Object> annotations;

    @Data
    public static class ResourceTemplate {
        /**
         * URI 模板（如：file:///{path}）
         */
        private String uriTemplate;

        /**
         * 模板描述
         */
        private String description;

        /**
         * MIME 类型
         */
        private String mimeType;

        /**
         * 模板参数列表
         */
        private Map<String, ResourceTemplateParam> params;
    }

    @Data
    public static class ResourceTemplateParam {
        /**
         * 参数名
         */
        private String name;

        /**
         * 参数描述
         */
        private String description;

        /**
         * 是否必需
         */
        private boolean required;

        /**
         * 参数类型
         */
        private String type;
    }
}
