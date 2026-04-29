package com.zgw.core.server;

import com.zgw.core.config.ConfigManager;
import com.zgw.core.config.GatewayConfig;
import com.zgw.core.http.HttpClient;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Gateway HTTP Server based on Netty
 */
public class GatewayServer {

    private static final Logger logger = LoggerFactory.getLogger(GatewayServer.class);
    public static final AttributeKey<HttpClient> HTTP_CLIENT_KEY = AttributeKey.valueOf("httpClient");

    private final GatewayConfig config;
    private final HttpClient httpClient;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private volatile boolean started = false;

    public GatewayServer(GatewayConfig config) {
        this.config = config;
        this.httpClient = new HttpClient(
                config.getServer().getConnectTimeout(),
                config.getServer().getReadTimeout()
        );
    }

    public GatewayServer() {
        this(ConfigManager.getInstance().getConfig());
    }

    /**
     * Start the gateway server
     */
    public synchronized void start() throws Exception {
        if (started) {
            logger.warn("Gateway server is already started");
            return;
        }

        logger.info("Starting Z-GW Gateway Server...");
        logger.info("Configuration: {}", config.getServer());

        // Create event loop groups
        GatewayConfig.ServerConfig serverConfig = config.getServer();
        if (Epoll.isAvailable()) {
            logger.info("Using Epoll event loop");
            bossGroup = new EpollEventLoopGroup(serverConfig.getBossThreads(),
                    new DefaultThreadFactory("gw-boss"));
            workerGroup = new EpollEventLoopGroup(serverConfig.getWorkerThreads(),
                    new DefaultThreadFactory("gw-worker"));
        } else {
            logger.info("Using NIO event loop");
            bossGroup = new NioEventLoopGroup(serverConfig.getBossThreads(),
                    new DefaultThreadFactory("gw-boss"));
            workerGroup = new NioEventLoopGroup(serverConfig.getWorkerThreads(),
                    new DefaultThreadFactory("gw-worker"));
        }

        // Create server bootstrap
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, serverConfig.getSoBacklog())
                .option(ChannelOption.SO_KEEPALIVE, serverConfig.isSoKeepalive())
                .childOption(ChannelOption.TCP_NODELAY, serverConfig.isTcpNodelay())
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // Store HTTP client in channel attribute
                        ch.attr(HTTP_CLIENT_KEY).set(httpClient);

                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline
                                // HTTP codec
                                .addLast(new HttpServerCodec())
                                // HTTP content aggregator
                                .addLast(new HttpObjectAggregator(serverConfig.getMaxContentLength()))
                                // Gateway request handler
                                .addLast(new GatewayHandler(config, httpClient));
                    }
                });

        // Bind and start
        InetSocketAddress address = new InetSocketAddress(serverConfig.getPort());
        ChannelFuture future = bootstrap.bind(address).sync();
        serverChannel = future.channel();

        started = true;
        logger.info("Z-GW Gateway Server started successfully on port {}", serverConfig.getPort());

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    /**
     * Shutdown the gateway server
     */
    public synchronized void shutdown() {
        if (!started) {
            return;
        }

        logger.info("Shutting down Z-GW Gateway Server...");

        try {
            if (httpClient != null) {
                httpClient.shutdown();
            }

            if (serverChannel != null) {
                serverChannel.close().syncUninterruptibly();
            }

            if (bossGroup != null) {
                bossGroup.shutdownGracefully().syncUninterruptibly();
            }

            if (workerGroup != null) {
                workerGroup.shutdownGracefully().syncUninterruptibly();
            }

            started = false;
            logger.info("Z-GW Gateway Server shutdown complete");
        } catch (Exception e) {
            logger.error("Error during shutdown", e);
        }
    }

    /**
     * Check if server is started
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Wait for server to terminate
     */
    public void awaitTermination() throws InterruptedException {
        if (serverChannel != null) {
            serverChannel.closeFuture().await();
        }
    }
}