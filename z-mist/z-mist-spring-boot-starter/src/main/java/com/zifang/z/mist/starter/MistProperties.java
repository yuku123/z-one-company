package com.zifang.z.mist.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Mist 配置属性
 */
@ConfigurationProperties(prefix = "z-mist")
public class MistProperties {

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 服务端地址
     */
    private String serverHost = "localhost";

    /**
     * 服务端端口
     */
    private int serverPort = 9085;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用密钥
     */
    private String appSecret;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}