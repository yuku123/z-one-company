package com.zifang.z.rpc.spring.boot.starter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Z-RPC 配置属性
 */
@ConfigurationProperties(prefix = "z.rpc")
public class ZRpcProperties {

    /**
     * 是否启用 Z-RPC
     */
    private boolean enabled = true;

    /**
     * 应用名称
     */
    private String application = "z-rpc-app";

    /**
     * 注册中心配置
     */
    private RegistryConfig registry = new RegistryConfig();

    /**
     * 协议配置
     */
    private ProtocolConfig protocol = new ProtocolConfig();

    /**
     * 服务端配置
     */
    private ServerConfig server = new ServerConfig();

    /**
     * 消费者配置
     */
    private ConsumerConfig consumer = new ConsumerConfig();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public RegistryConfig getRegistry() {
        return registry;
    }

    public void setRegistry(RegistryConfig registry) {
        this.registry = registry;
    }

    public ProtocolConfig getProtocol() {
        return protocol;
    }

    public void setProtocol(ProtocolConfig protocol) {
        this.protocol = protocol;
    }

    public ServerConfig getServer() {
        return server;
    }

    public void setServer(ServerConfig server) {
        this.server = server;
    }

    public ConsumerConfig getConsumer() {
        return consumer;
    }

    public void setConsumer(ConsumerConfig consumer) {
        this.consumer = consumer;
    }

    /**
     * 注册中心配置
     */

    public static class RegistryConfig {

        /**
         * 注册中心地址
         */
        private String address = "127.0.0.1:8084";

        /**
         * 命名空间
         */
        private String namespace = "public";

        /**
         * 是否启用注册中心
         */
        private boolean enabled = true;

        /**
         * 注册中心连接超时（毫秒）
         */
        private int timeout = 3000;

        /**
         * 会话超时时间（毫秒）
         */
        private int sessionTimeout = 60000;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public int getSessionTimeout() {
            return sessionTimeout;
        }

        public void setSessionTimeout(int sessionTimeout) {
            this.sessionTimeout = sessionTimeout;
        }
    }

    /**
     * 协议配置
     */

    public static class ProtocolConfig {

        /**
         * 协议名称
         */
        private String name = "z-rpc";

        /**
         * 序列化方式
         */
        private String serialization = "java";

        /**
         * 字符集
         */
        private String charset = "UTF-8";

        /**
         * 传输框架
         */
        private String transporter = "netty";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSerialization() {
            return serialization;
        }

        public void setSerialization(String serialization) {
            this.serialization = serialization;
        }

        public String getCharset() {
            return charset;
        }

        public void setCharset(String charset) {
            this.charset = charset;
        }

        public String getTransporter() {
            return transporter;
        }

        public void setTransporter(String transporter) {
            this.transporter = transporter;
        }
    }

    /**
     * 服务端配置
     */
    public static class ServerConfig {

        /**
         * 服务端口
         */
        private int port = 20880;

        /**
         * 服务主机地址
         */
        private String host;

        /**
         * 线程池大小
         */
        private int threads = 200;

        /**
         * IO 线程数
         */
        private int iothreads = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);

        /**
         * 接受连接数
         */
        private int accepts = 0;

        /**
         * 空闲超时时间（毫秒）
         */
        private int idleTimeout = 600000;

        /**
         * 是否启用服务
         */
        private boolean enabled = true;

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getThreads() {
            return threads;
        }

        public void setThreads(int threads) {
            this.threads = threads;
        }

        public int getIothreads() {
            return iothreads;
        }

        public void setIothreads(int iothreads) {
            this.iothreads = iothreads;
        }

        public int getAccepts() {
            return accepts;
        }

        public void setAccepts(int accepts) {
            this.accepts = accepts;
        }

        public int getIdleTimeout() {
            return idleTimeout;
        }

        public void setIdleTimeout(int idleTimeout) {
            this.idleTimeout = idleTimeout;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    /**
     * 消费者配置
     */
    public static class ConsumerConfig {

        /**
         * 调用超时（毫秒）
         */
        private int timeout = 3000;

        /**
         * 重试次数
         */
        private int retries = 2;

        /**
         * 负载均衡策略
         */
        private String loadbalance = "random";

        /**
         * 集群容错策略
         */
        private String cluster = "failover";

        /**
         * 是否异步调用
         */
        private boolean async = false;

        /**
         * 是否单向调用
         */
        private boolean oneway = false;

        /**
         * 检查服务提供者是否存在
         */
        private boolean check = true;

        /**
         * 延迟初始化
         */
        private boolean lazy = false;

        /**
         * 连接数限制
         */
        private int connections = 0;

        /**
         * 客户端类型
         */
        private String client = "netty";

        /**
         * 是否启用消费端
         */
        private boolean enabled = true;

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public int getRetries() {
            return retries;
        }

        public void setRetries(int retries) {
            this.retries = retries;
        }

        public String getLoadbalance() {
            return loadbalance;
        }

        public void setLoadbalance(String loadbalance) {
            this.loadbalance = loadbalance;
        }

        public String getCluster() {
            return cluster;
        }

        public void setCluster(String cluster) {
            this.cluster = cluster;
        }

        public boolean isAsync() {
            return async;
        }

        public void setAsync(boolean async) {
            this.async = async;
        }

        public boolean isOneway() {
            return oneway;
        }

        public void setOneway(boolean oneway) {
            this.oneway = oneway;
        }

        public boolean isCheck() {
            return check;
        }

        public void setCheck(boolean check) {
            this.check = check;
        }

        public boolean isLazy() {
            return lazy;
        }

        public void setLazy(boolean lazy) {
            this.lazy = lazy;
        }

        public int getConnections() {
            return connections;
        }

        public void setConnections(int connections) {
            this.connections = connections;
        }

        public String getClient() {
            return client;
        }

        public void setClient(String client) {
            this.client = client;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
