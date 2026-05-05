package com.zifang.z.ctc.web.api;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.domain.entity.User;
import com.zifang.ctc.core.service.UserBizService;
import com.zifang.ctc.core.service.model.request.ChangePasswordRequest;
import com.zifang.ctc.core.service.model.request.ResetPasswordRequest;
import com.zifang.ctc.core.service.model.response.UserInfoResponse;
import com.zifang.ctc.sso.JwtUtil;
import com.zifang.ctc.core.service.model.request.UserPageReq;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
    
    public UserInfoResponse getCurrentUserInfo(@RequestHeader("Authorization") String token) {
        JwtUtil.VerificationResult result = jwtUtil.verifyToken(token.replace("Bearer ", ""));
        if (!result.isValid()) {
            return new UserInfoResponse();
        }
        
        Map<String, Object> claims = result.getClaims();
        Long userId = ((Number) claims.get("userId")).longValue();
        
        User user = userBizService.getById(userId);
        if (user == null) {
            return new UserInfoResponse();
        }
        
        UserInfoResponse response = new UserInfoResponse();
        response.setUserId(user.getId());
        response.setUserName(user.getUserName());
        response.setRealName(user.getRealName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setTenantId(user.getTenantCode());
        response.setDeptId(user.getDeptId());
        response.setRoles(userBizService.getUserRoles(userId));
        
        return response;
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取用户列表")

    public List<User> listUsers() {
        return userBizService.list();
    }

    /**
     * 分页查询用户
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询用户")
    public IPage<User> pageUsers(@RequestBody UserPageReq req) {
        return userBizService.page(req);
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户")
    
    public User getUserById(@PathVariable Long id) {
        return userBizService.getById(id);
    }

    /**
     * 创建用户
     */
    @PostMapping
    @Operation(summary = "创建用户")
    
    public void createUser(@RequestBody User user) {
        userBizService.create(user);
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    
    public void updateUser(@PathVariable Long id, @RequestBody User user) {
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
}
