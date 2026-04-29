package com.zifang.z.oss.api.config;

import com.zifang.z.oss.common.exception.OssException;
import com.zifang.z.oss.core.domain.entity.OssUser;
import com.zifang.z.oss.core.domain.service.IOssUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证拦截器
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    public static final String HEADER_ACCESS_KEY = "X-Zoss-Access-Key";
    public static final String HEADER_SECRET_KEY = "X-Zoss-Secret-Key";
    public static final String ATTR_USER = "oss_user";

    @Autowired
    private IOssUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String accessKey = request.getHeader(HEADER_ACCESS_KEY);
        String secretKey = request.getHeader(HEADER_SECRET_KEY);

        if (accessKey == null || secretKey == null) {
            // 尝试从参数获取
            accessKey = request.getParameter("access_key");
            secretKey = request.getParameter("secret_key");
        }

        if (accessKey == null || secretKey == null) {
            throw new OssException(401, "Missing credentials");
        }

        OssUser user = userService.getByAccessKey(accessKey);
        if (user == null) {
            throw new OssException(401, "Invalid access key");
        }

        if (!user.getSecretKey().equals(secretKey)) {
            throw new OssException(401, "Invalid secret key");
        }

        if (user.getStatus() != 1) {
            throw new OssException(403, "User is disabled");
        }

        // 将用户信息存入请求属性
        request.setAttribute(ATTR_USER, user);
        return true;
    }
}