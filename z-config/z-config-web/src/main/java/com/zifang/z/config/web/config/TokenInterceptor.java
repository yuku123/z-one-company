package com.zifang.z.config.web.config;

import org.springframework.stereotype.Component;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 全局Token拦截器（Spring 5.3+推荐：直接实现HandlerInterceptor接口）
 */
@Component
public class TokenInterceptor implements HandlerInterceptor { // 关键修改：实现接口而非继承适配器

    // Token在Cookie中的key（可配置在application.yml中）
    private static final String COOKIE_TOKEN_KEY = "token";
    // Token在Header中的key（兼容Bearer Token格式）
    private static final String HEADER_TOKEN_KEY = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * 请求进入Controller之前执行（核心拦截逻辑）
     * 接口默认方法，重写即可（与原preHandle逻辑完全一致）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 初始化上下文
        TokenContext.clear();

        // 2. 从Cookie中获取Token
        String token = getTokenFromCookie(request);

        // 3. Cookie中无Token时，从Header获取（兼容多端场景）
        if (token == null || token.isEmpty()) {
            token = getTokenFromHeader(request);
        }

        // 4. 处理Token（根据业务需求调整）
        if (token != null && !token.isEmpty()) {
            TokenContext.setToken(token); // 存入上下文
            // 验证并解析Token（JWT/Redis/数据库，替换为实际逻辑）
            TokenContext.UserInfo userInfo = verifyAndParseToken(token);
            if (userInfo != null) {
                TokenContext.setUserInfo(userInfo); // 存入解析后的用户信息
            } else {
                // Token无效：返回401未授权（自定义响应格式）
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"code\":401,\"msg\":\"Invalid Token\",\"data\":null}");
                return false;
            }
        } else {
            // 无Token：公开接口放行，否则拦截
            if (!isPublicUrl(request.getRequestURI())) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"code\":401,\"msg\":\"Token Required\",\"data\":null}");
                return false;
            }
        }

        // 5. 放行请求到Controller
        return true;
    }

    /**
     * 请求处理完成后执行：清除ThreadLocal（关键，避免内存泄漏）
     * 接口默认方法，重写即可（与原afterCompletion逻辑一致）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        TokenContext.clear();
    }

    /**
     * 从Cookie中获取Token
     */
    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (COOKIE_TOKEN_KEY.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 从Header中获取Token（兼容Bearer格式）
     */
    private String getTokenFromHeader(HttpServletRequest request) {
        String headerValue = request.getHeader(HEADER_TOKEN_KEY);
        if (headerValue == null || !headerValue.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return headerValue.substring(BEARER_PREFIX.length()).trim();
    }

    /**
     * 验证并解析Token（示例：JWT解析逻辑，替换为实际实现）
     */
    private TokenContext.UserInfo verifyAndParseToken(String token) {
        try {
            // 实际场景：JWT解析（需引入jjwt依赖）、Redis校验、数据库查询
            // 示例模拟有效Token解析结果
            TokenContext.UserInfo userInfo = new TokenContext.UserInfo();
            userInfo.setUserId(1001L);
            userInfo.setUsername("admin");
            return userInfo;
        } catch (Exception e) {
            // Token过期、签名错误等，返回null表示无效
            return null;
        }
    }

    /**
     * 判断是否为公开接口（无需Token）
     */
    private boolean isPublicUrl(String requestUri) {
        return requestUri.startsWith("/api/auth/login")
                || requestUri.startsWith("/api/auth/register")
                || requestUri.startsWith("/api/auth/logout")
                || requestUri.startsWith("/api/public/")
                || requestUri.startsWith("/auth/")  // TASK001: 认证相关接口
                || requestUri.equals("/")
                || requestUri.equals("/index.html")
                || requestUri.equals("/login.html")
                || requestUri.startsWith("/static/")
                || requestUri.startsWith("/css/")
                || requestUri.startsWith("/js/")
                || requestUri.startsWith("/images/")
                || requestUri.endsWith(".css")
                || requestUri.endsWith(".js")
                || requestUri.endsWith(".png")
                || requestUri.endsWith(".jpg")
                || requestUri.endsWith(".ico")
                || requestUri.equals("/favicon.ico");
    }
}