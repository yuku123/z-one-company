package com.zifang.z.mq.client.producer;

import com.zifang.z.mq.common.message.Message;
import com.zifang.z.mq.common.message.MessageExt;

/**
 * 默认MQ生产者
 */
public class DefaultMQProducer {

    private String producerGroup;
    private String namesrvAddr;

    public DefaultMQProducer(String producerGroup) {
        this.producerGroup = producerGroup;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public void start() throws Exception {
        // TODO: 实现启动逻辑
    }

    public void shutdown() throws Exception {
        // TODO: 实现关闭逻辑
    }

    public SendResult send(Message message) throws Exception {
        // TODO: 实现发送逻辑
        return new SendResult();
    }

    public void sendOneway(Message message) throws Exception {
        // TODO: 实现单向发送逻辑
    }

    public void send(Message message, SendCallback sendCallback) throws Exception {
        // TODO: 实现异步发送逻辑
    }
}
