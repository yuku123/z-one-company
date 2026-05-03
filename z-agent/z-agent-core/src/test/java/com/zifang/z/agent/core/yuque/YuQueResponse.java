package com.zifang.z.agent.core.yuque;
import lombok.Data;

/**
 * 语雀API通用返回结果
 */
@Data
public class YuQueResponse<T> {
    private boolean success;
    private T data;
    private String errorMsg;
}