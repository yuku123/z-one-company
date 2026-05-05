package com.zifang.z.ctc.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.domain.entity.User;
import com.zifang.ctc.core.service.UserBizService;
import com.zifang.ctc.core.service.dto.UserDTO;
import com.zifang.ctc.core.service.model.request.ChangePasswordRequest;
import com.zifang.ctc.core.service.model.request.ResetPasswordRequest;
import com.zifang.ctc.core.service.model.request.UserPageReq;
import com.zifang.z.ctc.web.api.request.UserReq;
import com.zifang.z.ctc.web.api.response.UserResp;
import com.zifang.ctc.sso.JwtUtil;
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
@RequestMapping("/account")
@Tag(name = "账号管理")
public class AccountManagerController {

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private UserBizService userBizService;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息")
    public UserResp getCurrentUserInfo(@RequestHeader("Authorization") String token) {
        JwtUtil.VerificationResult result = jwtUtil.verifyToken(token.replace("Bearer ", ""));
        if (!result.isValid()) {
            return null;
        }
        Map<String, Object> claims = result.getClaims();
        Long userId = ((Number) claims.get("userId")).longValue();
        return toResp(userBizService.getById(userId));
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取用户列表")
    public List<UserResp> listUsers() {
        return userBizService.list().stream()
                .map(this::toResp)
                .collect(Collectors.toList());
    }

    /**
     * 分页查询用户
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询用户")
    public IPage<UserResp> pageUsers(@RequestBody UserPageReq req) {
        IPage<UserDTO> page = userBizService.page(req);
        return page.convert(this::toResp);
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户")
    public UserResp getUserById(@PathVariable Long id) {
        return toResp(userBizService.getById(id));
    }

    /**
     * 创建用户
     */
    @PostMapping
    @Operation(summary = "创建用户")
    public void createUser(@RequestBody UserReq req) {
        User user = toEntity(req);
        userBizService.create(user);
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    public void updateUser(@PathVariable Long id, @RequestBody UserReq req) {
        User user = toEntity(req);
        user.setId(id);
        userBizService.update(user);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public void deleteUser(@PathVariable Long id) {
        userBizService.delete(id);
    }

    /**
     * 分配角色给用户
     */
    @PostMapping("/{userId}/roles")
    @Operation(summary = "分配角色给用户")
    public void assignRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        userBizService.assignRoles(userId, roleIds);
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    @Operation(summary = "修改密码")
    public void changePassword(@RequestBody ChangePasswordRequest request) {
        userBizService.changePassword(request.getUserId(), request.getOldPassword(), request.getNewPassword());
    }

    /**
     * 重置密码
     */
    @PostMapping("/{userId}/reset-password")
    @Operation(summary = "重置密码")
    public void resetPassword(@PathVariable Long userId, @RequestBody ResetPasswordRequest request) {
        userBizService.resetPassword(userId, request.getNewPassword());
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
