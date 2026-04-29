package com.zifang.z.mq.broker;

import com.zifang.z.mq.remoting.netty.NettyRemotingServer;
import com.zifang.z.mq.remoting.netty.NettyServerConfig;
import com.zifang.z.mq.store.MessageStoreConfig;
import com.zifang.z.mq.store.log.CommitLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Broker控制器
 * Broker是消息存储和转发的核心节点，负责：
 * 1. 消息存储 - 将消息持久化到CommitLog
 * 2. 消息转发 - 向Consumer推送消息
 * 3. 消息查询 - 根据条件查询消息
 * 4. 心跳维护 - 向NameServer注册和维持心跳
 */
public class BrokerController {

    private static final Logger log = LoggerFactory.getLogger(BrokerController.class);

    // Broker配置
    private final BrokerConfig brokerConfig;

    // 消息存储配置
    private final MessageStoreConfig messageStoreConfig;

    // Netty服务端配置
    private final NettyServerConfig nettyServerConfig;

    // 运行标志
    private final AtomicBoolean running = new AtomicBoolean(false);

    // Netty服务端（处理客户端请求）
    private NettyRemotingServer remotingServer;

    // 定时任务调度器
    private ScheduledExecutorService scheduledExecutorService;

    // 业务线程池
    private ExecutorService sendMessageExecutor;
    private ExecutorService pullMessageExecutor;
    private ExecutorService adminBrokerExecutor;

    // CommitLog
    private CommitLog commitLog;

    public BrokerController(
            BrokerConfig brokerConfig,
            MessageStoreConfig messageStoreConfig,
            NettyServerConfig nettyServerConfig) {
        this.brokerConfig = brokerConfig;
        this.messageStoreConfig = messageStoreConfig;
        this.nettyServerConfig = nettyServerConfig;
    }

    /**
     * 初始化Broker
     */
    public boolean initialize() {
        try {
            // 加载CommitLog
            this.commitLog = new CommitLog(this.messageStoreConfig);
            if (!this.commitLog.load()) {
                log.error("load commitlog failed");
                return false;
            }

            // 初始化Netty服务端
            this.remotingServer = new NettyRemotingServer(this.nettyServerConfig);

            // 注册处理器
            this.registerProcessor();

            // 初始化定时任务
            this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "BrokerScheduledThread");
                t.setDaemon(true);
                return t;
            });

            // 初始化业务线程池
            this.sendMessageExecutor = Executors.newFixedThreadPool(
                    this.brokerConfig.getSendMessageThreadPoolNums(),
                    r -> new Thread(r, "SendMessageThread_"));

            this.pullMessageExecutor = Executors.newFixedThreadPool(
                    this.brokerConfig.getPullMessageThreadPoolNums(),
                    r -> new Thread(r, "PullMessageThread_"));

            this.adminBrokerExecutor = Executors.newFixedThreadPool(
                    this.brokerConfig.getAdminBrokerThreadPoolNums(),
                    r -> new Thread(r, "AdminBrokerThread_"));

            log.info("Broker initialize successfully");
            return true;
        } catch (Exception e) {
            log.error("Broker initialize failed", e);
            return false;
        }
    }

    /**
     * 注册处理器
     */
    private void registerProcessor() {
        // 注册发送消息处理器
        // this.remotingServer.registerProcessor(RequestCode.SEND_MESSAGE, new SendMessageProcessor(this), this.sendMessageExecutor);

        // 注册拉取消息处理器
        // this.remotingServer.registerProcessor(RequestCode.PULL_MESSAGE, new PullMessageProcessor(this), this.pullMessageExecutor);

        // 注册管理命令处理器
        // this.remotingServer.registerProcessor(RequestCode.UPDATE_AND_CREATE_TOPIC, new AdminBrokerProcessor(this), this.adminBrokerExecutor);
    }

    /**
     * 启动Broker
     */
    public void start() throws Exception {
        if (this.running.compareAndSet(false, true)) {
            // 启动CommitLog
            this.commitLog.start();

            // 启动Netty服务端
            this.remotingServer.start();

            // 注册Broker到NameServer（如果配置了NameServer地址）
            if (this.brokerConfig.getNamesrvAddr() != null && !this.brokerConfig.getNamesrvAddr().isEmpty()) {
                this.registerBrokerAll(true, false);
            }

            log.info("Broker started successfully");
        }
    }

    /**
     * 停止Broker
     */
    public void shutdown() {
        if (this.running.compareAndSet(true, false)) {
            // 停止Netty服务端
            this.remotingServer.shutdown();

            // 停止CommitLog
            this.commitLog.shutdown();

            // 关闭线程池
            this.scheduledExecutorService.shutdown();
            this.sendMessageExecutor.shutdown();
            this.pullMessageExecutor.shutdown();
            this.adminBrokerExecutor.shutdown();

            log.info("Broker shutdown successfully");
        }
    }

    /**
     * 向所有NameServer注册Broker
     */
    private void registerBrokerAll(boolean checkOrderConfig, boolean oneway) {
        // 实现注册逻辑
        log.info("Register broker to NameServer: {}", this.brokerConfig.getNamesrvAddr());
    }

    // Getters
    public BrokerConfig getBrokerConfig() {
        return brokerConfig;
    }

    public MessageStoreConfig getMessageStoreConfig() {
        return messageStoreConfig;
    }

    public CommitLog getCommitLog() {
        return commitLog;
    }
}

/**
 * Broker配置
 */
class BrokerConfig {
    private String namesrvAddr = System.getenv("NAMESRV_ADDR");
    private String brokerName = System.getProperty("brokerName", "DEFAULT_BROKER");
    private String brokerClusterName = System.getProperty("brokerClusterName", "DEFAULT_CLUSTER");
    private int sendMessageThreadPoolNums = 16;
    private int pullMessageThreadPoolNums = 16;
    private int adminBrokerThreadPoolNums = 4;

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public String getBrokerClusterName() {
        return brokerClusterName;
    }

    public void setBrokerClusterName(String brokerClusterName) {
        this.brokerClusterName = brokerClusterName;
    }

    public int getSendMessageThreadPoolNums() {
        return sendMessageThreadPoolNums;
    }

    public void setSendMessageThreadPoolNums(int sendMessageThreadPoolNums) {
        this.sendMessageThreadPoolNums = sendMessageThreadPoolNums;
    }

    public int getPullMessageThreadPoolNums() {
        return pullMessageThreadPoolNums;
    }

    public void setPullMessageThreadPoolNums(int pullMessageThreadPoolNums) {
        this.pullMessageThreadPoolNums = pullMessageThreadPoolNums;
    }

    public int getAdminBrokerThreadPoolNums() {
        return adminBrokerThreadPoolNums;
    }

    public void setAdminBrokerThreadPoolNums(int adminBrokerThreadPoolNums) {
        this.adminBrokerThreadPoolNums = adminBrokerThreadPoolNums;
    }
}

/**
 * Broker注册结果
 */
class RegisterBrokerResult {
    private String haServerAddr;
    private String oldAddr;

    public String getHaServerAddr() {
        return haServerAddr;
    }

    public void setHaServerAddr(String haServerAddr) {
        this.haServerAddr = haServerAddr;
    }

    public String getOldAddr() {
        return oldAddr;
    }

    public void setOldAddr(String oldAddr) {
        this.oldAddr = oldAddr;
    }
}
