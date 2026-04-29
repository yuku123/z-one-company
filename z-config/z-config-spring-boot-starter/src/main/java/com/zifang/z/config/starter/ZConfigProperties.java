package com.zifang.z.config.starter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

/**
 * ZConfig 配置属性
 */
@ConfigurationProperties(prefix = "base.z.config")
public class ZConfigProperties {

    /**
     * 配置中心服务地址
     */
    private String serverAddr = "127.0.0.1:8848";

    /**
     * 连接超时时间（毫秒）
     */
    private int timeout = 3000;

    /**
     * 命名空间
     */
    private String namespace = "public";

    public Properties asProperties() {
        Properties properties = new Properties();
        properties.setProperty(serverAddr, getServerAddr());
        return properties;
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}