package com.zifang.z.agent.mcp.starter.protocol;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * MCP 服务器能力声明
 */
@Data
public class McpServerCapabilities {

    /**
     * 协议版本
     */
    private String protocolVersion = McpProtocolConstants.PROTOCOL_VERSION;

    /**
     * 服务器信息
     */
    private ServerInfo serverInfo;

    /**
     * 能力集
     */
    private Capabilities capabilities;

    @Data
    public static class ServerInfo {
        private String name = "z-agent-mcp-server";
        private String version = "1.0.0";
    }

    @Data
    public static class Capabilities {
        /**
         * 工具能力
         */
        private ToolCapabilities tools;

        /**
         * 资源能力
         */
        private ResourceCapabilities resources;

        /**
         * 提示能力
         */
        private PromptCapabilities prompts;

        /**
         * 采样能力
         */
        private SamplingCapabilities sampling;

        /**
         * 是否支持日志
         */
        private boolean logging;

        /**
         * 实验性功能
         */
        private Map<String, Object> experimental;
    }

    @Data
    public static class ToolCapabilities {
        /**
         * 是否支持列表变更通知
         */
        private boolean listChanged;
    }

    @Data
    public static class ResourceCapabilities {
        /**
         * 是否支持订阅
         */
        private boolean subscribe;

        /**
         * 是否支持列表变更通知
         */
        private boolean listChanged;
    }

    @Data
    public static class PromptCapabilities {
        /**
         * 是否支持列表变更通知
         */
        private boolean listChanged;
    }

    @Data
    public static class SamplingCapabilities {
        /**
         * 支持的模型列表
         */
        private List<String> models;
    }

    /**
     * 创建默认能力配置
     */
    public static McpServerCapabilities createDefault() {
        McpServerCapabilities caps = new McpServerCapabilities();

        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setName("z-agent-mcp-server");
        serverInfo.setVersion("1.0.0");
        caps.setServerInfo(serverInfo);

        Capabilities capabilities = new Capabilities();

        ToolCapabilities toolCaps = new ToolCapabilities();
        toolCaps.setListChanged(true);
        capabilities.setTools(toolCaps);

        ResourceCapabilities resourceCaps = new ResourceCapabilities();
        resourceCaps.setSubscribe(true);
        resourceCaps.setListChanged(true);
        capabilities.setResources(resourceCaps);

        PromptCapabilities promptCaps = new PromptCapabilities();
        promptCaps.setListChanged(true);
        capabilities.setPrompts(promptCaps);

        capabilities.setLogging(true);

        caps.setCapabilities(capabilities);

        return caps;
    }
}
