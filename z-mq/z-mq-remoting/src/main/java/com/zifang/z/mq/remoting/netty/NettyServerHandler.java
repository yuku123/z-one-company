package com.zifang.z.mq.remoting.netty;

import com.zifang.z.mq.remoting.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty Server Handler
 * 处理入站消息
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RemotingCommand> {

    private static final Logger log = LoggerFactory.getLogger(NettyServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RemotingCommand msg) throws Exception {
        // 交由 NettyRemotingServer 处理
        processMessageReceived(ctx, msg);
    }

    /**
     * 处理接收到的消息
     * 此方法会被 NettyRemotingServer 重写
     */
    protected void processMessageReceived(ChannelHandlerContext ctx, RemotingCommand msg) {
        // 默认实现：记录日志
        log.debug("Received message: {}", msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Exception caught in NettyServerHandler: {}", cause.getMessage(), cause);
        ctx.close();
    }
}
