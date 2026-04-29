package com.zifang.ctc.core.service.model.request;

/**
 * 重置密码请求
 */
public class ResetPasswordRequest {

    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
