package com.zifang.ctc.core.service.model.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 登录响应
 */
@Schema(description = "登录响应")
public class LoginResponse {

    @Schema(description = "访问令牌")
    private String token;

    @Schema(description = "令牌类型")
    private String tokenType = "Bearer";

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "过期时间（秒）")
    private long expiresIn;

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public long getExpiresIn() {
        return expiresIn;
    }
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
}