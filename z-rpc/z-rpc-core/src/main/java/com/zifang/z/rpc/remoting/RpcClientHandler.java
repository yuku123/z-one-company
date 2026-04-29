package com.zifang.z.rpc.remoting;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * RPC 客户端处理器
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Map<String, CompletableFuture<RpcResponse>> pendingRequests;

    public RpcClientHandler(Map<String, CompletableFuture<RpcResponse>> pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) {
        CompletableFuture<RpcResponse> future = pendingRequests.remove(response.getRequestId());
        if (future != null) {
            future.complete(response);
            log.debug("Received response for request: {}", response.getRequestId());
        } else {
            log.warn("Received unknown response: {}", response.getRequestId());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("RPC client exception: {}", cause.getMessage(), cause);

        // 通知所有待处理的请求
        for (CompletableFuture<RpcResponse> future : pendingRequests.values()) {
            future.completeExceptionally(cause);
        }
        pendingRequests.clear();

        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.warn("Channel inactive, connection closed");

        // 通知所有待处理的请求
        for (CompletableFuture<RpcResponse> future : pendingRequests.values()) {
            future.completeExceptionally(new RuntimeException("Connection closed"));
        }
        pendingRequests.clear();
    }
}
