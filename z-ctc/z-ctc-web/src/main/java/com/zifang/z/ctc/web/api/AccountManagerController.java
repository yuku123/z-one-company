package com.zifang.z.ctc.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.domain.entity.User;
import com.zifang.ctc.core.service.UserBizService;
import com.zifang.ctc.core.service.dto.UserDTO;
import com.zifang.ctc.core.service.model.request.ChangePasswordRequest;
import com.zifang.ctc.core.service.model.request.ResetPasswordRequest;
import com.zifang.ctc.core.service.model.request.UserPageReq;
import com.zifang.ctc.sso.JwtUtil;
import com.zifang.util.core.meta.Result;
import com.zifang.z.ctc.web.api.request.UserReq;
import com.zifang.z.ctc.web.api.response.UserResp;
import org.springframework.beans.BeanUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 账号管理控制器 - 4A账户管理模块
 */
@RestController
@RequestMapping("/api/account")
@Tag(name = "账号管理")
public class AccountManagerController {

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private UserBizService userBizService;

    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息")
    public Result<UserResp> getCurrentUserInfo(@RequestHeader("Authorization") String token) {
        JwtUtil.VerificationResult result = jwtUtil.verifyToken(token.replace("Bearer ", ""));
        if (!result.isValid()) {
            return Result.success(null);
        }
        Map<String, Object> claims = result.getClaims();
        Long userId = ((Number) claims.get("userId")).longValue();
        return Result.success(toResp(userBizService.getById(userId)));
    }

    @GetMapping("/list")
    @Operation(summary = "获取用户列表")
    public Result<List<UserResp>> listUsers() {
        List<UserResp> data = userBizService.list().stream()
                .map(this::toResp)
                .collect(Collectors.toList());
        return Result.success(data);
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询用户")
    public Result<IPage<UserResp>> pageUsers(@RequestBody UserPageReq req) {
        return Result.success(userBizService.page(req).convert(this::toResp));
    }

    @GetMapping("/get")
    @Operation(summary = "根据ID获取用户")
    public Result<UserResp> getUserById(@RequestParam Long id) {
        return Result.success(toResp(userBizService.getById(id)));
    }

    @PostMapping
    @Operation(summary = "创建用户")
    public Result<Void> createUser(@RequestBody UserReq req) {
        User user = toEntity(req);
        userBizService.create(user);
        return Result.success();
    }

    @PostMapping("/{id}/update")
    @Operation(summary = "更新用户")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody UserReq req) {
        User user = toEntity(req);
        user.setId(id);
        userBizService.update(user);
        return Result.success();
    }

    @PostMapping("/{id}/delete")
    @Operation(summary = "删除用户")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userBizService.delete(id);
        return Result.success();
    }

    @PostMapping("/{userId}/assign-role")
    @Operation(summary = "分配角色给用户")
    public Result<Void> assignRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        userBizService.assignRoles(userId, roleIds);
        return Result.success();
    }

    @PostMapping("/change-password")
    @Operation(summary = "修改密码")
    public Result<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        userBizService.changePassword(request.getUserId(), request.getOldPassword(), request.getNewPassword());
        return Result.success();
    }

    @PostMapping("/{userId}/reset-password")
    @Operation(summary = "重置密码")
    public Result<Void> resetPassword(@PathVariable Long userId, @RequestBody ResetPasswordRequest request) {
        userBizService.resetPassword(userId, request.getNewPassword());
        return Result.success();
    }

    private UserResp toResp(UserDTO dto) {
        if (dto == null) return null;
        UserResp resp = new UserResp();
        BeanUtils.copyProperties(dto, resp);
        return resp;
    }

    private User toEntity(UserReq req) {
        if (req == null) return null;
        User user = new User();
        BeanUtils.copyProperties(req, user);
        return user;
    }
}
