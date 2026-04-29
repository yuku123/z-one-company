package com.zifang.z.mq.client.producer;

import com.zifang.z.mq.common.message.Message;
import com.zifang.z.mq.common.message.MessageExt;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DefaultMQProducer 单元测试
 */
public class DefaultMQProducerTest {

    private DefaultMQProducer producer;

    @BeforeEach
    public void setUp() {
        producer = new DefaultMQProducer("TestProducerGroup");
        producer.setNamesrvAddr("localhost:9876");
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (producer != null) {
            producer.shutdown();
        }
    }

    @Test
    public void testConstructorWithGroup() {
        assertNotNull(producer);
        assertEquals("TestProducerGroup", producer.getProducerGroup());
    }

    @Test
    public void testDefaultValues() {
        assertNotNull(producer.getNamesrvAddr());
        assertNull(producer.getNamesrvAddr());
    }

    @Test
    public void testSetAndGetNamesrvAddr() {
        producer.setNamesrvAddr("192.168.1.100:9876");
        assertEquals("192.168.1.100:9876", producer.getNamesrvAddr());
    }

    @Test
    public void testSetAndGetProducerGroup() {
        producer.setProducerGroup("NewGroup");
        assertEquals("NewGroup", producer.getProducerGroup());
    }

    @Test
    public void testSendMessageWithoutStart() {
        // Create a message
        Message message = new Message();
        message.setTopic("TestTopic");
        message.setBody("TestBody".getBytes(StandardCharsets.UTF_8));

        // Attempt to send without starting the producer should throw an exception
        assertThrows(Exception.class, () -> {
            producer.send(message);
        });
    }

    @Test
    public void testSendOnewayWithoutStart() {
        // Create a message
        Message message = new Message();
        message.setTopic("TestTopic");
        message.setBody("TestBody".getBytes(StandardCharsets.UTF_8));

        // Attempt to send oneway without starting the producer should throw an exception
        assertThrows(Exception.class, () -> {
            producer.sendOneway(message);
        });
    }

    @Test
    public void testSendAsyncWithoutStart() {
        // Create a message
        Message message = new Message();
        message.setTopic("TestTopic");
        message.setBody("TestBody".getBytes(StandardCharsets.UTF_8));

        // Attempt to send async without starting the producer should throw an exception
        assertThrows(Exception.class, () -> {
            producer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                }

                @Override
                public void onException(Throwable e) {
                }
            });
        });
    }

    @Test
    public void testCreateMessage() {
        String topic = "TestTopic";
        byte[] body = "TestBody".getBytes(StandardCharsets.UTF_8);

        Message message = new Message(topic, body);

        assertNotNull(message);
        assertEquals(topic, message.getTopic());
        assertArrayEquals(body, message.getBody());
    }

    @Test
    public void testCreateMessageWithTags() {
        String topic = "TestTopic";
        String tags = "TagA||TagB";
        byte[] body = "TestBody".getBytes(StandardCharsets.UTF_8);

        Message message = new Message(topic, tags, body);

        assertNotNull(message);
        assertEquals(topic, message.getTopic());
        assertEquals(tags, message.getTags());
        assertArrayEquals(body, message.getBody());
    }

    @Test
    public void testCreateMessageWithTagsAndKeys() {
        String topic = "TestTopic";
        String tags = "TagA";
        String keys = "Key001";
        byte[] body = "TestBody".getBytes(StandardCharsets.UTF_8);

        Message message = new Message(topic, tags, keys, body);

        assertNotNull(message);
        assertEquals(topic, message.getTopic());
        assertEquals(tags, message.getTags());
        assertEquals(keys, message.getKeys());
        assertArrayEquals(body, message.getBody());
    }

    @Test
    public void testMessageProperties() {
        Message message = new Message();
        message.setTopic("TestTopic");
        message.setBody("TestBody".getBytes(StandardCharsets.UTF_8));

        // Set properties
        message.putProperty("key1", "value1");
        message.putProperty("key2", "value2");

        assertEquals("value1", message.getProperty("key1"));
        assertEquals("value2", message.getProperty("key2"));
        assertNull(message.getProperty("nonexistent"));
    }

    @Test
    public void testMessageExtProperties() {
        MessageExt messageExt = new MessageExt();
        messageExt.setTopic("TestTopic");
        messageExt.setQueueId(1);
        messageExt.setQueueOffset(100);
        messageExt.setMsgId("TestMsgId");

        assertEquals("TestTopic", messageExt.getTopic());
        assertEquals(1, messageExt.getQueueId());
        assertEquals(100, messageExt.getQueueOffset());
        assertEquals("TestMsgId", messageExt.getMsgId());
    }
}
