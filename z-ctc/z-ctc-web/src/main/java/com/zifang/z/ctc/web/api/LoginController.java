package com.zifang.z.ctc.web.api;


import com.zifang.ctc.core.domain.entity.User;
import com.zifang.ctc.core.service.dto.UserDTO;
import com.zifang.ctc.core.service.UserBizService;
import com.zifang.ctc.core.service.model.request.LoginRequest;
import com.zifang.ctc.core.service.model.request.RegisterRequest;
import com.zifang.ctc.core.service.model.response.LoginResponse;
import com.zifang.ctc.sso.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录认证控制器 - 4A中心认证模块
 */
@RestController
@RequestMapping("/auth")
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
    public LoginResponse login(@RequestBody LoginRequest request) {
        // 验证用户凭据
        UserDTO user = userBizService.authenticate(request.getUserName(), request.getPassword());

        // 生成JWT令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUserName());
        claims.put("tenantId", user.getTenantCode());
        claims.put("roles", user.getRoles());

        String token = jwtUtil.generateToken(claims, 86400); // 24小时过期

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUserName(user.getUserName());
        response.setExpiresIn(86400);
        return response;
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public void register(@RequestBody RegisterRequest request) {
        userBizService.register(request);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public void logout(@RequestHeader("Authorization") String token) {
        // 将token加入黑名单或从缓存中移除
        userBizService.logout(token);
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌")
    public LoginResponse refreshToken(@RequestHeader("Authorization") String token) {
        return userBizService.refreshToken(token);
    }

    /**
     * 发送注册验证码
     */
    @PostMapping("/register/send-code")
    @Operation(summary = "发送注册验证码")
    public Map<String, Object> sendRegisterCode(
            @Parameter(description = "接收者(手机号/邮箱)") @RequestParam String receiver,
            @Parameter(description = "验证码类型: PHONE/EMAIL") @RequestParam String codeType) {
        Map<String, Object> result = new HashMap<>();
        try {
            String code = userBizService.sendRegisterCode(receiver, codeType);
            // 实际应该调用z-msg发送短信/邮件，这里直接返回验证码（仅测试用）
            result.put("code", 0);
            result.put("message", "发送成功");
            result.put("data", code); // 测试用，生产应删除
        } catch (Exception e) {
            result.put("code", 1);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 手机注册
     */
    @PostMapping("/register/phone")
    @Operation(summary = "手机注册")
    public LoginResponse registerByPhone(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        String code = request.get("code");
        String password = request.get("password");
        return userBizService.registerByPhone(phone, code, password);
    }

    /**
     * 邮箱注册
     */
    @PostMapping("/register/email")
    @Operation(summary = "邮箱注册")
    public LoginResponse registerByEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        String password = request.get("password");
        return userBizService.registerByEmail(email, code, password);
    }

    /**
     * 用户名密码注册
     */
    @PostMapping("/register/username")
    @Operation(summary = "用户名密码注册")
    public LoginResponse registerByUsername(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        return userBizService.registerByUsername(username, password);
    }

    /**
     * 手机登录
     */
    @PostMapping("/login/phone")
    @Operation(summary = "手机验证码登录")
    public LoginResponse loginByPhone(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        String code = request.get("code");
        return userBizService.loginByPhone(phone, code);
    }

    /**
     * 发送重置密码验证码
     */
    @PostMapping("/reset-password/send-code")
    @Operation(summary = "发送重置密码验证码")
    public Map<String, Object> sendResetPasswordCode(
            @Parameter(description = "接收者(手机号/邮箱)") @RequestParam String receiver,
            @Parameter(description = "验证码类型: PHONE/EMAIL") @RequestParam String codeType) {
        Map<String, Object> result = new HashMap<>();
        try {
            userBizService.sendResetPasswordCode(receiver, codeType);
            result.put("code", 0);
            result.put("message", "发送成功");
        } catch (Exception e) {
            result.put("code", 1);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 手机找回密码
     */
    @PostMapping("/reset-password/phone")
    @Operation(summary = "手机找回密码")
    public Map<String, Object> resetPasswordByPhone(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String phone = request.get("phone");
            String code = request.get("code");
            String newPassword = request.get("newPassword");
            userBizService.resetPasswordByPhone(phone, code, newPassword);
            result.put("code", 0);
            result.put("message", "密码重置成功");
        } catch (Exception e) {
            result.put("code", 1);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 邮箱找回密码
     */
    @PostMapping("/reset-password/email")
    @Operation(summary = "邮箱找回密码")
    public Map<String, Object> resetPasswordByEmail(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String email = request.get("email");
            String code = request.get("code");
            String newPassword = request.get("newPassword");
            userBizService.resetPasswordByEmail(email, code, newPassword);
            result.put("code", 0);
            result.put("message", "密码重置成功");
        } catch (Exception e) {
            result.put("code", 1);
            result.put("message", e.getMessage());
        }
        return result;
    }
}