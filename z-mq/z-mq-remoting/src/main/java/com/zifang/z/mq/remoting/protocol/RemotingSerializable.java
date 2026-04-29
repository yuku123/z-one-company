package com.zifang.z.mq.remoting.protocol;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;

import java.nio.charset.StandardCharsets;

/**
 * 远程通信序列化接口
 * 提供对象与字节数组/JSON字符串之间的转换
 */
public abstract class RemotingSerializable {

    /**
     * 将对象编码为字节数组（JSON格式）
     */
    public byte[] encode() {
        return JSON.toJSONBytes(this);
    }

    /**
     * 将对象编码为JSON字符串
     */
    public String toJson() {
        return JSON.toJSONString(this);
    }

    /**
     * 从字节数组解码对象
     */
    public static <T> T decode(byte[] data, Class<T> clazz) {
        if (data == null || data.length == 0) {
            return null;
        }
        return JSON.parseObject(data, clazz);
    }

    /**
     * 从字节数组解码对象（带泛型）
     */
    public static <T> T decode(byte[] data, TypeReference<T> typeReference) {
        if (data == null || data.length == 0) {
            return null;
        }
        String json = new String(data, StandardCharsets.UTF_8);
        return JSON.parseObject(json, typeReference);
    }

    /**
     * 从字符串解码对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        return JSON.parseObject(json, clazz);
    }

    /**
     * 从字符串解码对象（带泛型）
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        return JSON.parseObject(json, typeReference);
    }

    /**
     * 将对象转换为字节数组（静态方法）
     */
    public static byte[] encode(Object obj) {
        if (obj == null) {
            return null;
        }
        return JSON.toJSONBytes(obj);
    }

    /**
     * 将对象转换为JSON字符串（静态方法）
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        return JSON.toJSONString(obj);
    }
}
