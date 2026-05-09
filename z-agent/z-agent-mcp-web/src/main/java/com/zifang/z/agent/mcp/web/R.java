package com.zifang.z.agent.mcp.web;

/**
 * 轻量统一响应（与 z-util Result 一致）
 */
public class R<T> {

    private int code;
    private String message;
    private T data;

    public static <T> R<T> success() { return success(null); }

    public static <T> R<T> success(T data) {
        R<T> r = new R<>();
        r.code = 0;
        r.message = "success";
        r.data = data;
        return r;
    }

    public static <T> R<T> fail(String msg) {
        R<T> r = new R<>();
        r.code = 500;
        r.message = msg;
        return r;
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
