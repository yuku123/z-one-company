package com.zifang.ctc.sso.config;


import com.zifang.ctc.sso.model.UserInfo;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class SsoInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;
    private final SsoProperties ssoProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public SsoInterceptor(TokenService tokenService, SsoProperties ssoProperties) {
        this.tokenService = tokenService;
        this.ssoProperties = ssoProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String requestUri = request.getRequestURI();

        // 检查是否为忽略的路径
        if (isExcludePath(requestUri)) {
            return true;
        }

        // 检查是否需要拦截的路径
        if (!isInterceptPath(requestUri)) {
            return true;
        }

        // 也从Header中获取token
        String token = getTokenFromCookie(request);
        if (token == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            } else {
                token = authHeader;
            }
        }

        // 验证token
        UserInfo userInfo = tokenService.verifyToken(token);
        if (userInfo != null) {
            // token有效，将用户信息存入请求属性
            request.setAttribute("ssoUser", userInfo);
            return true;
        }

        // token无效或不存在，重定向到登录页，并携带当前地址作为回调
        String redirectUrl = ssoProperties.getLoginUrl() + "?redirect=" +
                request.getRequestURL().toString();
        response.sendRedirect(redirectUrl);
        return false;
    }

    // 从Cookie中获取token
    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ssoProperties.getTokenCookieName().equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // 判断是否为忽略的路径
    private boolean isExcludePath(String requestUri) {
        List<String> excludePaths = ssoProperties.getExcludePaths();
        for (String pattern : excludePaths) {
            if (pathMatcher.match(pattern, requestUri)) {
                return true;
            }
        }
        return false;
    }

    // 判断是否为需要拦截的路径
    private boolean isInterceptPath(String requestUri) {
        List<String> interceptPaths = ssoProperties.getInterceptPaths();
        for (String pattern : interceptPaths) {
            if (pathMatcher.match(pattern, requestUri)) {
                return true;
            }
        }
        return false;
    }
}