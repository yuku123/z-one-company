package com.zifang.ctc.sso.config;

import com.zifang.ctc.sso.model.UserInfo;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class SsoContext {

    private SsoContext() {
        // 私有构造，防止实例化
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息，未登录则返回null
     */
    public static UserInfo getCurrentUser() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }

        HttpServletRequest request = attributes.getRequest();
        return (UserInfo) request.getAttribute("ssoUser");
    }

    /**
     * 判断当前用户是否已登录
     *
     * @return 是否登录
     */
    public static boolean isLoggedIn() {
        return getCurrentUser() != null;
    }
}