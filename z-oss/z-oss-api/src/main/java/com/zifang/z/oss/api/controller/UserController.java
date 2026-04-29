package com.zifang.z.oss.api.controller;

import com.zifang.z.oss.api.dto.UserLoginRequest;
import com.zifang.z.oss.api.dto.UserRegisterRequest;
import com.zifang.z.oss.api.vo.UserVO;
import com.zifang.z.oss.core.domain.entity.OssUser;
import com.zifang.z.oss.core.domain.service.IOssUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理API控制器
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private IOssUserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserVO> register(@RequestBody UserRegisterRequest request) {
        OssUser user = userService.register(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(convertUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserLoginRequest request) {
        OssUser user = userService.login(request.getUsername(), request.getPassword());
        Map<String, Object> result = new HashMap<>();
        result.put("token", user.getAccessKey() + ":" + user.getSecretKey());
        result.put("user", convertUser(user));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/info")
    public ResponseEntity<UserVO> getUserInfo(@RequestHeader("X-Zoss-Access-Key") String accessKey) {
        OssUser user = userService.getByAccessKey(accessKey);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertUser(user));
    }

    @PutMapping("/info")
    public ResponseEntity<UserVO> updateUserInfo(@RequestHeader("X-Zoss-Access-Key") String accessKey,
                                                  @RequestBody UserRegisterRequest request) {
        OssUser user = userService.updateUser(accessKey, request.getUsername());
        return ResponseEntity.ok(convertUser(user));
    }

    @PostMapping("/reset-key")
    public ResponseEntity<UserVO> resetKeys(@RequestHeader("X-Zoss-Access-Key") String accessKey) {
        OssUser user = userService.resetAccessKey(accessKey);
        return ResponseEntity.ok(convertUser(user));
    }

    @PostMapping("/password")
    public ResponseEntity<Void> changePassword(@RequestHeader("X-Zoss-Access-Key") String accessKey,
                                                @RequestBody Map<String, String> passwordMap) {
        String oldPassword = passwordMap.get("oldPassword");
        String newPassword = passwordMap.get("newPassword");
        userService.changePassword(accessKey, oldPassword, newPassword);
        return ResponseEntity.ok().build();
    }

    private UserVO convertUser(OssUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setAccessKey(user.getAccessKey());
        vo.setSecretKey(user.getSecretKey());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}