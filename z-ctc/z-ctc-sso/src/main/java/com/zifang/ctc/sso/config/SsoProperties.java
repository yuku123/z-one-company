package com.zifang.ctc.sso.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "sso")
public class SsoProperties {

    // 登录页地址
    private String loginUrl = "http://localhost:8080/login";
    // token在Cookie中的名称
    private String tokenCookieName = "sso_token";

    // 拦截的路径模式
    private List<String> interceptPaths = new ArrayList<String>() {{
        add("/**");
    }};

    // 忽略拦截的路径模式
    private List<String> excludePaths = new ArrayList<>();

    // token验证服务地址
    private String authServerUrl = "http://localhost:8080/auth/verify";

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getTokenCookieName() {
        return tokenCookieName;
    }

    public void setTokenCookieName(String tokenCookieName) {
        this.tokenCookieName = tokenCookieName;
    }

    public List<String> getInterceptPaths() {
        return interceptPaths;
    }

    public void setInterceptPaths(List<String> interceptPaths) {
        this.interceptPaths = interceptPaths;
    }

    public List<String> getExcludePaths() {
        return excludePaths;
    }

    public void setExcludePaths(List<String> excludePaths) {
        this.excludePaths = excludePaths;
    }

    public String getAuthServerUrl() {
        return authServerUrl;
    }

    public void setAuthServerUrl(String authServerUrl) {
        this.authServerUrl = authServerUrl;
    }
}
