package com.zifang.z.agent.mcp.starter.protocol;

import lombok.Data;

import java.util.Map;

/**
 * MCP 内容类型（用于资源和提示）
 */
@Data
public class McpContent {

    /**
     * 内容类型：text, image, resource, audio, video
     */
    private String type;

    /**
     * 文本内容（当 type=text 时）
     */
    private String text;

    /**
     * MIME 类型（当 type=image/audio/video/resource 时）
     */
    private String mimeType;

    /**
     * Base64 编码的数据（当 type=image/audio/video 时）
     */
    private String data;

    /**
     * 资源 URI（当 type=resource 时）
     */
    private String uri;

    /**
     * 资源元数据
     */
    private Map<String, Object> metadata;

    public static McpContent text(String text) {
        McpContent content = new McpContent();
        content.setType("text");
        content.setText(text);
        return content;
    }

    public static McpContent image(String mimeType, String base64Data) {
        McpContent content = new McpContent();
        content.setType("image");
        content.setMimeType(mimeType);
        content.setData(base64Data);
        return content;
    }

    public static McpContent resource(String uri, String mimeType) {
        McpContent content = new McpContent();
        content.setType("resource");
        content.setUri(uri);
        content.setMimeType(mimeType);
        return content;
    }
}
