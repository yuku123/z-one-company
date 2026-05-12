package com.zifang.z.task.core.common.result;

/**
 * 业务结果码
 */
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    BUSINESS_ERROR(400, "业务异常"),
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "资源不存在"),
    FORBIDDEN(403, "无权限访问"),
    INTERNAL_SERVER_ERROR(500, "系统繁忙，请稍后再试");

    private final Integer code;
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
