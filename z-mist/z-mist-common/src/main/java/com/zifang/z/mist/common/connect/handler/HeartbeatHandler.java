package com.zifang.z.mist.common.connect.handler;

import com.zifang.z.mist.common.connect.CommandType;
import com.zifang.z.mist.common.connect.message.HeartbeatMessage;
import com.zifang.z.mist.common.connect.message.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 心跳处理器
 */
public class HeartbeatHandler extends IdleStateHandler {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatHandler.class);

    private static final int HEARTBEAT_INTERVAL = 30;

    public HeartbeatHandler() {
        super(HEARTBEAT_INTERVAL, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        logger.debug("Channel idle: {}", ctx.channel());
        // 发送心跳响应
        HeartbeatMessage heartbeat = new HeartbeatMessage();
        heartbeat.setCommandType(CommandType.HEARTBEAT_RESPONSE);
        heartbeat.setSuccess(true);
        ctx.writeAndFlush(heartbeat);
    }
}