package com.zifang.z.task.common.result;

/**
 * 响应状态码枚举
 *
 * @author zifang
 */
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    FAIL(500, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未登录或登录已过期"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "无权限访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 请求方式不支持
     */
    METHOD_NOT_ALLOWED(405, "请求方式不支持"),

    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(408, "请求超时"),

    /**
     * 资源冲突
     */
    CONFLICT(409, "资源冲突"),

    /**
     * 服务端错误
     */
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    /**
     * 服务不可用
     */
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    /**
     * 业务错误
     */
    BUSINESS_ERROR(1001, "业务处理失败"),

    /**
     * 数据已存在
     */
    DATA_EXIST(1002, "数据已存在"),

    /**
     * 数据不存在
     */
    DATA_NOT_EXIST(1003, "数据不存在");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态消息
     */
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
