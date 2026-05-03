package com.zifang.z.agent.mcp.starter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java 8 快捷构建 Map/List 的工具类
 */
public class MapListUtil {
    // 构建空 Map
    public static <K, V> Map<K, V> map() {
        return new HashMap<>();
    }

    // 构建单个键值对的 Map
    public static <K, V> Map<K, V> map(K k1, V v1) {
        Map<K, V> map = new HashMap<>();
        map.put(k1, v1);
        return map;
    }

    // 构建两个键值对的 Map
    public static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2) {
        Map<K, V> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    // 构建三个键值对的 Map
    public static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2, K k3, V v3) {
        Map<K, V> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    // 构建空 List
    public static <T> List<T> list() {
        return new ArrayList<>();
    }

    // 构建单个元素的 List
    public static <T> List<T> list(T t1) {
        List<T> list = new ArrayList<>();
        list.add(t1);
        return list;
    }

    // 构建多个元素的 List
    @SafeVarargs
    public static <T> List<T> list(T... elements) {
        List<T> list = new ArrayList<>();
        for (T t : elements) {
            list.add(t);
        }
        return list;
    }
}