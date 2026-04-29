package com.zifang.z.mq.client.producer;

/**
 * 发送状态
 */
public enum SendStatus {
    /**
     * 发送成功
     */
    SEND_OK,

    /**
     * 刷盘超时
     */
    FLUSH_DISK_TIMEOUT,

    /**
     * 刷盘失败
     */
    FLUSH_SLAVE_TIMEOUT,

    /**
     * Slave不可用
     */
    SLAVE_NOT_AVAILABLE
}
