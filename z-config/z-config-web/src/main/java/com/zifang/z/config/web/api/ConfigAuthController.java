package com.zifang.z.config.web.api;

import com.zifang.util.core.meta.Result;
import com.zifang.z.config.common.model.auth.LoginRequest;
import com.zifang.z.config.common.model.auth.LoginResponse;
import com.zifang.z.config.core.domain.entity.ZUsers;
import com.zifang.z.config.core.domain.service.IZUsersService;
import com.zifang.z.config.web.config.TokenContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.UUID;

/**
 * 配置中心专属认证（临时方案，最终应接入 z-ctc）
 */
@RestController
@RequestMapping("/api/config-auth")
@Tag(name = "配置中心认证")
public class ConfigAuthController {

    @Autowired
    private IZUsersService usersService;

    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin";
    private static final int TOKEN_MAX_AGE = 24 * 60 * 60;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        String username = request.getUsername();
        String password = request.getPassword();

        if (DEFAULT_USERNAME.equals(username) && DEFAULT_PASSWORD.equals(password)) {
            String token = generateToken();
            setTokenCookie(response, token);
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(token);
            loginResponse.setUsername(username);
            loginResponse.setNickname("管理员");
            return Result.success(loginResponse);
        }

        ZUsers user = usersService.getById(username);
        if (user == null || !user.getEnabled()) {
            return Result.fail("用户名或密码错误");
        }
        if (!password.equals(user.getPassword())) {
            return Result.fail("用户名或密码错误");
        }

        String token = generateToken();
        setTokenCookie(response, token);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setUsername(username);
        loginResponse.setNickname(user.getUsername());
        return Result.success(loginResponse);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public Result<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        TokenContext.clear();
        return Result.success();
    }

    @GetMapping("/current")
    @Operation(summary = "获取当前用户信息")
    public Result<LoginResponse> getCurrentUser() {
        TokenContext.UserInfo userInfo = TokenContext.getUserInfo();
        if (userInfo == null) {
            return Result.fail("未登录");
        }
        LoginResponse response = new LoginResponse();
        response.setUsername(userInfo.getUsername());
        response.setNickname(userInfo.getUsername());
        return Result.success(response);
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private void setTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(TOKEN_MAX_AGE);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}
