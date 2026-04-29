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
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理")
public class AuthController {

    @Autowired
    private IZUsersService usersService;

    /**
     * 默认登录账号
     */
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin";

    /**
     * Token有效期（小时）
     */
    private static final int TOKEN_MAX_AGE = 24 * 60 * 60;

    /**
     * 登录接口
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        String username = request.getUsername();
        String password = request.getPassword();

        // 默认账号登录
        if (DEFAULT_USERNAME.equals(username) && DEFAULT_PASSWORD.equals(password)) {
            String token = generateToken();
            setTokenCookie(response, token);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(token);
            loginResponse.setUsername(username);
            loginResponse.setNickname("管理员");
            return Result.success(loginResponse);
        }

        // 数据库账号登录
        ZUsers user = usersService.getById(username);
        if (user == null || !user.getEnabled()) {
            return Result.fail("用户名或密码错误");
        }

        // TODO: 密码加密验证（使用BCrypt）
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

    /**
     * 登出接口
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public Result<Void> logout(HttpServletResponse response) {
        // 清除Cookie
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        // 清除上下文
        TokenContext.clear();

        return Result.success();
    }

    /**
     * 获取当前登录用户信息
     */
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

    /**
     * 生成Token
     */
    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 设置Token Cookie
     */
    private void setTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(TOKEN_MAX_AGE);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        // cookie.setSecure(true); // 生产环境启用HTTPS时开启
        response.addCookie(cookie);
    }
}
