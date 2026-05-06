package com.zifang.ctc.sso.config;

import com.zifang.ctc.sso.JwtUtil;
import com.zifang.ctc.sso.model.UserInfo;

/**
 * 本地 Token 验证服务 - 直接用 JwtUtil 验证，不走 HTTP 调用
 * 适用于 All-in-One 模式（z-ctc 与各模块在同一 JVM）
 */
public class LocalTokenService implements TokenService {

    private final JwtUtil jwtUtil;

    public LocalTokenService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserInfo verifyToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        try {
            JwtUtil.VerificationResult result = jwtUtil.verifyToken(token);
            if (!result.isValid()) {
                return null;
            }
            java.util.Map<String, Object> claims = result.getClaims();
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(String.valueOf(claims.getOrDefault("userId", "")));
            userInfo.setUsername(String.valueOf(claims.getOrDefault("username", "")));
            userInfo.setNickname(String.valueOf(claims.getOrDefault("nickname", "")));
            return userInfo;
        } catch (Exception e) {
            return null;
        }
    }
}
