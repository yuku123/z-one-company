package com.zifang.z.ctc.web.api;

import com.zifang.ctc.core.service.dto.UserDTO;
import com.zifang.ctc.core.service.UserBizService;
import com.zifang.ctc.core.service.model.request.LoginRequest;
import com.zifang.ctc.core.service.model.request.RegisterRequest;
import com.zifang.ctc.core.service.model.response.LoginResponse;
import com.zifang.ctc.sso.JwtUtil;
import com.zifang.ctc.sso.model.UserInfo;
import com.zifang.util.core.meta.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录认证控制器 - 4A中心认证模块
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理")
public class LoginController {

    @Resource
    private UserBizService userBizService;

    @Resource
    private JwtUtil jwtUtil;

    /**
     * 用户登录 - 4A认证
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        UserDTO user = userBizService.authenticate(request.getUserName(), request.getPassword());

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUserName());
        claims.put("tenantId", user.getTenantCode());
        claims.put("roles", user.getRoles());

        String token = jwtUtil.generateToken(claims, 86400);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUserName(user.getUserName());
        response.setExpiresIn(86400);
        return Result.success(response);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<Void> register(@RequestBody RegisterRequest request) {
        userBizService.register(request);
        return Result.success();
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        userBizService.logout(token);
        return Result.success();
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌")
    public Result<LoginResponse> refreshToken(@RequestHeader("Authorization") String token) {
        LoginResponse response = userBizService.refreshToken(token);
        return Result.success(response);
    }

    /**
     * 发送注册验证码
     */
    @PostMapping("/register/send-code")
    @Operation(summary = "发送注册验证码")
    public Result<String> sendRegisterCode(
            @Parameter(description = "接收者(手机号/邮箱)") @RequestParam String receiver,
            @Parameter(description = "验证码类型: PHONE/EMAIL") @RequestParam String codeType) {
        String code = userBizService.sendRegisterCode(receiver, codeType);
        return Result.success(code);
    }

    /**
     * 手机注册
     */
    @PostMapping("/register/phone")
    @Operation(summary = "手机注册")
    public Result<LoginResponse> registerByPhone(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        String code = request.get("code");
        String password = request.get("password");
        LoginResponse response = userBizService.registerByPhone(phone, code, password);
        return Result.success(response);
    }

    /**
     * 邮箱注册
     */
    @PostMapping("/register/email")
    @Operation(summary = "邮箱注册")
    public Result<LoginResponse> registerByEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        String password = request.get("password");
        LoginResponse response = userBizService.registerByEmail(email, code, password);
        return Result.success(response);
    }

    /**
     * 用户名密码注册
     */
    @PostMapping("/register/username")
    @Operation(summary = "用户名密码注册")
    public Result<LoginResponse> registerByUsername(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        LoginResponse response = userBizService.registerByUsername(username, password);
        return Result.success(response);
    }

    /**
     * 手机登录
     */
    @PostMapping("/login/phone")
    @Operation(summary = "手机验证码登录")
    public Result<LoginResponse> loginByPhone(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        String code = request.get("code");
        LoginResponse response = userBizService.loginByPhone(phone, code);
        return Result.success(response);
    }

    /**
     * 发送重置密码验证码
     */
    @PostMapping("/reset-password/send-code")
    @Operation(summary = "发送重置密码验证码")
    public Result<Void> sendResetPasswordCode(
            @Parameter(description = "接收者(手机号/邮箱)") @RequestParam String receiver,
            @Parameter(description = "验证码类型: PHONE/EMAIL") @RequestParam String codeType) {
        userBizService.sendResetPasswordCode(receiver, codeType);
        return Result.success();
    }

    /**
     * 手机找回密码
     */
    @PostMapping("/reset-password/phone")
    @Operation(summary = "手机找回密码")
    public Result<Void> resetPasswordByPhone(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        String code = request.get("code");
        String newPassword = request.get("newPassword");
        userBizService.resetPasswordByPhone(phone, code, newPassword);
        return Result.success();
    }

    /**
     * 邮箱找回密码
     */
    @PostMapping("/reset-password/email")
    @Operation(summary = "邮箱找回密码")
    public Result<Void> resetPasswordByEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        String newPassword = request.get("newPassword");
        userBizService.resetPasswordByEmail(email, code, newPassword);
        return Result.success();
    }

    /**
     * Token 验证接口（供 sso-starter 的 RemoteTokenService 调用）
     * 注意：此接口不统一 Result 包装，sso-starter 依赖其原始返回格式
     */
    @GetMapping("/verify")
    @Operation(summary = "验证Token")
    public UserInfo verifyToken(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        JwtUtil.VerificationResult result = jwtUtil.verifyToken(token);
        if (!result.isValid()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
        Map<String, Object> claims = result.getClaims();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(String.valueOf(claims.getOrDefault("userId", "")));
        userInfo.setUsername(String.valueOf(claims.getOrDefault("username", "")));
        userInfo.setNickname(String.valueOf(claims.getOrDefault("nickname", "")));
        return userInfo;
    }
}
