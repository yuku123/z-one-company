package com.zifang.z.schedule.core.model;

import java.io.Serializable;

/**
 * 通用返回结果类
 *
 * @param <T> 数据类型
 */
public class ReturnT<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功状态码
     */
    public static final int SUCCESS_CODE = 200;

    /**
     * 失败状态码
     */
    public static final int FAIL_CODE = 500;

    /**
     * 状态码
     */
    private int code;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 返回数据
     */
    private T content;

    public ReturnT() {
    }

    public ReturnT(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ReturnT(int code, String msg, T content) {
        this.code = code;
        this.msg = msg;
        this.content = content;
    }

    public static <T> ReturnT<T> success() {
        return new ReturnT<>(SUCCESS_CODE, "success");
    }

    public static <T> ReturnT<T> success(String msg) {
        return new ReturnT<>(SUCCESS_CODE, msg);
    }

    public static <T> ReturnT<T> success(T content) {
        return new ReturnT<>(SUCCESS_CODE, "success", content);
    }

    public static <T> ReturnT<T> success(String msg, T content) {
        return new ReturnT<>(SUCCESS_CODE, msg, content);
    }

    public static <T> ReturnT<T> fail() {
        return new ReturnT<>(FAIL_CODE, "fail");
    }

    public static <T> ReturnT<T> fail(String msg) {
        return new ReturnT<>(FAIL_CODE, msg);
    }

    public static <T> ReturnT<T> fail(int code, String msg) {
        return new ReturnT<>(code, msg);
    }

    public boolean isSuccess() {
        return this.code == SUCCESS_CODE;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ReturnT{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", content=" + content +
                '}';
    }
}
