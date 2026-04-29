package com.zifang.z.mq.nameserver;

import com.zifang.z.mq.nameserver.kvconfig.KVConfigManager;
import com.zifang.z.mq.nameserver.routeinfo.RouteInfoManager;
import com.zifang.z.mq.remoting.netty.NettyRemotingServer;
import com.zifang.z.mq.remoting.netty.NettyServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * NameServer 控制器
 * NameServer是整个MQ集群的服务注册与发现中心，负责：
 * 1. Broker注册管理 - 接收Broker心跳，维护Broker列表
 * 2. 路由信息管理 - 维护Topic到Broker的路由关系
 * 3. 服务发现 - 为Producer和Consumer提供路由查询
 * 4. KV配置管理 - 管理全局配置项
 */
public class NameServerController {

    private static final Logger log = LoggerFactory.getLogger(NameServerController.class);

    // NameServer配置
    private final NamesrvConfig namesrvConfig;

    // Netty服务端配置
    private final NettyServerConfig nettyServerConfig;

    // 路由信息管理器
    private RouteInfoManager routeInfoManager;

    // KV配置管理器
    private KVConfigManager kvConfigManager;

    // Netty服务端
    private NettyRemotingServer remotingServer;

    // 网络事件执行器
    private ExecutorService remotingExecutor;

    // 定时任务调度器 - Broker通道扫描（每10秒）
    private ScheduledExecutorService scheduledExecutorService;

    // 定时任务调度器 - 打印KV配置（每10分钟）
    private ScheduledExecutorService scanExecutorService;

    // 运行标志
    private final AtomicBoolean running = new AtomicBoolean(false);

    public NameServerController(NamesrvConfig namesrvConfig, NettyServerConfig nettyServerConfig) {
        this.namesrvConfig = namesrvConfig;
        this.nettyServerConfig = nettyServerConfig;
    }

    /**
     * 初始化NameServer
     */
    public boolean initialize() {
        // 初始化KV配置管理器
        this.kvConfigManager = new KVConfigManager(namesrvConfig.getKvConfigPath());
        this.kvConfigManager.load();

        // 初始化路由信息管理器
        this.routeInfoManager = new RouteInfoManager();

        // 初始化Netty服务端
        this.remotingServer = new NettyRemotingServer(this.nettyServerConfig);

        // 注册默认请求处理器 - 暂时注释掉，等待DefaultRequestProcessor实现
        // this.remotingServer.registerDefaultProcessor(
        //         new DefaultRequestProcessor(this),
        //         this.remotingExecutor);

        // 初始化网络事件执行器
        this.remotingExecutor = Executors.newFixedThreadPool(
                nettyServerConfig.getServerWorkerThreads(),
                r -> new Thread(r, "NameServerExecutorThread_" + ThreadLocalRandom.current().nextInt(1000)));

        // 初始化定时任务调度器 - Broker通道扫描
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(
                r -> new Thread(r, "NameServerScheduledThread"));

        // 初始化定时任务调度器 - 打印KV配置
        this.scanExecutorService = Executors.newSingleThreadScheduledExecutor(
                r -> new Thread(r, "NameServerScanThread"));

        // 注册定时任务 - 每10秒扫描不活跃的Broker
        this.scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                NameServerController.this.routeInfoManager.scanNotActiveBroker();
            } catch (Exception e) {
                log.error("scanNotActiveBroker exception", e);
            }
        }, 5, 10, TimeUnit.SECONDS);

        // 注册定时任务 - 每10分钟打印KV配置
        this.scanExecutorService.scheduleAtFixedRate(() -> {
            try {
                NameServerController.this.kvConfigManager.printAllPeriodically();
            } catch (Exception e) {
                log.error("printAllPeriodically exception", e);
            }
        }, 1, 10, TimeUnit.MINUTES);

        return true;
    }

    /**
     * 启动NameServer
     */
    public void start() throws Exception {
        if (this.running.compareAndSet(false, true)) {
            this.remotingServer.start();
            log.info("NameServer started successfully");
        }
    }

    /**
     * 关闭NameServer
     */
    public void shutdown() {
        if (this.running.compareAndSet(true, false)) {
            // 关闭定时任务
            this.scheduledExecutorService.shutdown();
            this.scanExecutorService.shutdown();

            // 关闭Netty服务端
            this.remotingServer.shutdown();

            // 关闭执行器
            this.remotingExecutor.shutdown();

            // 保存KV配置
            this.kvConfigManager.persist();

            log.info("NameServer shutdown successfully");
        }
    }

    public NamesrvConfig getNamesrvConfig() {
        return namesrvConfig;
    }

    public NettyServerConfig getNettyServerConfig() {
        return nettyServerConfig;
    }

    public RouteInfoManager getRouteInfoManager() {
        return routeInfoManager;
    }

    public KVConfigManager getKvConfigManager() {
        return kvConfigManager;
    }
}

/**
 * NameServer配置
 */
class NamesrvConfig {
    private String kvConfigPath = System.getProperty("user.home") + File.separator + "zmq" + File.separator + "namesrv" + File.separator + "kvConfig.json";

    public String getKvConfigPath() {
        return kvConfigPath;
    }

    public void setKvConfigPath(String kvConfigPath) {
        this.kvConfigPath = kvConfigPath;
    }
}
