package com.zifang.ctc.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.ctc.core.domain.entity.Role;
import com.zifang.ctc.core.domain.entity.User;
import com.zifang.ctc.core.domain.entity.UserRole;
import com.zifang.ctc.core.domain.service.IUserService;
import com.zifang.ctc.core.domain.service.IRoleService;
import com.zifang.ctc.core.domain.service.IVerifyCodeService;
import com.zifang.ctc.core.service.UserBizService;
import com.zifang.ctc.core.service.dto.UserDTO;
import com.zifang.ctc.core.service.dto.converter.UserDtoConverter;
import com.zifang.ctc.core.service.model.request.RegisterRequest;
import com.zifang.ctc.core.service.model.response.LoginResponse;
import com.zifang.ctc.core.service.model.request.UserPageReq;
import com.zifang.ctc.sso.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserBizServiceImpl implements UserBizService {

    @Resource
    private IUserService userService;

    @Resource
    private IRoleService roleService;

    @Resource
    private IVerifyCodeService verifyCodeService;

    private final JwtUtil jwtUtil = new JwtUtil("ctc-secret-key-2024-secure-jwt-signing-key");

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("123456"));
    }

    @Override
    public UserDTO authenticate(String userName, String password) {
        User user = userService.selectByUserName(userName);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用");
        }

        user.setLastLoginTime(LocalDateTime.now());
        userService.updateById(user);

        return UserDtoConverter.toDTO(user);
    }

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (userService.countByUserName(request.getUserName()) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUserName(request.getUserName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(1);
        user.setGmtCreate(LocalDateTime.now());
        user.setGmtModified(LocalDateTime.now());

        userService.save(user);
    }

    @Override
    public void logout(String token) {
    }

    @Override
    public LoginResponse refreshToken(String token) {
        JwtUtil.VerificationResult result = jwtUtil.verifyToken(token.replace("Bearer ", ""));
        if (!result.isValid()) {
            throw new RuntimeException("Token无效或已过期");
        }

        Map<String, Object> claims = result.getClaims();
        String newToken = jwtUtil.generateToken(claims, 86400);

        LoginResponse response = new LoginResponse();
        response.setToken(newToken);
        response.setUserId(Long.valueOf(claims.get("userId").toString()));
        response.setUserName((String) claims.get("username"));
        response.setExpiresIn(86400);

        return response;
    }

    @Override
    public UserDTO getById(Long id) {
        User user = userService.getById(id);
        if (user == null) return null;
        return UserDtoConverter.toDTO(user, getUserRoles(id));
    }

    @Override
    public List<UserDTO> list() {
        return userService.list(new LambdaQueryWrapper<User>().orderByDesc(User::getGmtCreate)).stream()
                .map(UserDtoConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public IPage<UserDTO> page(UserPageReq req) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .orderByDesc(User::getGmtCreate);

        if (req.getUserName() != null && !req.getUserName().isEmpty()) {
            wrapper.like(User::getUserName, req.getUserName());
        }
        if (req.getRealName() != null && !req.getRealName().isEmpty()) {
            wrapper.like(User::getRealName, req.getRealName());
        }
        if (req.getStatus() != null) {
            wrapper.eq(User::getStatus, req.getStatus());
        }
        if (req.getTenantCode() != null && !req.getTenantCode().isEmpty()) {
            wrapper.eq(User::getTenantCode, req.getTenantCode());
        }

        Page<User> page = new Page<>(req.getCurrent(), req.getSize());
        return userService.page(page, wrapper).convert(u -> UserDtoConverter.toDTO(u, getUserRoles(u.getId())));
    }

    @Override
    @Transactional
    public void create(User user) {
        if (userService.countByUserName(user.getUserName()) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        user.setGmtCreate(LocalDateTime.now());
        user.setGmtModified(LocalDateTime.now());

        userService.save(user);
    }

    @Override
    @Transactional
    public void update(User user) {
        user.setGmtModified(LocalDateTime.now());
        user.setPassword(null);
        user.setUserName(null);
        userService.updateById(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userService.removeById(id);
    }

    @Override
    public List<String> getUserRoles(Long userId) {
        return userService.getUserRoles(userId).stream()
                .map(Role::getRoleCode)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        userService.assignUserRoles(userId, roleIds);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setGmtModified(LocalDateTime.now());
        userService.updateById(user);
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setGmtModified(LocalDateTime.now());
        userService.updateById(user);
    }

    @Override
    public String sendRegisterCode(String receiver, String codeType) {
        if (!verifyCodeService.canSend(receiver)) {
            throw new RuntimeException("发送过于频繁，请稍后再试");
        }
        return verifyCodeService.generateCode(receiver, "REGISTER", codeType);
    }

    @Override
    public LoginResponse registerByPhone(String phone, String code, String password) {
        if (!verifyCodeService.verifyCode(phone, code, "REGISTER")) {
            throw new RuntimeException("验证码错误或已过期");
        }

        if (userService.selectByPhone(phone) != null) {
            throw new RuntimeException("该手机号已注册");
        }

        User user = new User();
        user.setUserName("phone_" + phone);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(1);
        user.setGmtCreate(LocalDateTime.now());
        user.setGmtModified(LocalDateTime.now());

        userService.save(user);

        return buildLoginResponse(user);
    }

    @Override
    public LoginResponse registerByEmail(String email, String code, String password) {
        if (!verifyCodeService.verifyCode(email, code, "REGISTER")) {
            throw new RuntimeException("验证码错误或已过期");
        }

        if (userService.selectByEmail(email) != null) {
            throw new RuntimeException("该邮箱已注册");
        }

        User user = new User();
        user.setUserName("email_" + email);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(1);
        user.setGmtCreate(LocalDateTime.now());
        user.setGmtModified(LocalDateTime.now());

        userService.save(user);

        return buildLoginResponse(user);
    }

    @Override
    public LoginResponse registerByUsername(String username, String password) {
        if (userService.countByUserName(username) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUserName(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(1);
        user.setGmtCreate(LocalDateTime.now());
        user.setGmtModified(LocalDateTime.now());

        userService.save(user);

        return buildLoginResponse(user);
    }

    @Override
    public LoginResponse loginByPhone(String phone, String code) {
        if (!verifyCodeService.verifyCode(phone, code, "LOGIN")) {
            throw new RuntimeException("验证码错误或已过期");
        }

        User user = userService.selectByPhone(phone);
        if (user == null) {
            throw new RuntimeException("用户不存在，请先注册");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用");
        }

        user.setLastLoginTime(LocalDateTime.now());
        userService.updateById(user);

        return buildLoginResponse(user);
    }

    @Override
    public void sendResetPasswordCode(String receiver, String codeType) {
        if (!verifyCodeService.canSend(receiver)) {
            throw new RuntimeException("发送过于频繁，请稍后再试");
        }
        verifyCodeService.generateCode(receiver, "RESET_PWD", codeType);
    }

    @Override
    public void resetPasswordByPhone(String phone, String code, String newPassword) {
        if (!verifyCodeService.verifyCode(phone, code, "RESET_PWD")) {
            throw new RuntimeException("验证码错误或已过期");
        }

        User user = userService.selectByPhone(phone);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setGmtModified(LocalDateTime.now());
        userService.updateById(user);
    }

    @Override
    public void resetPasswordByEmail(String email, String code, String newPassword) {
        if (!verifyCodeService.verifyCode(email, code, "RESET_PWD")) {
            throw new RuntimeException("验证码错误或已过期");
        }

        User user = userService.selectByEmail(email);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setGmtModified(LocalDateTime.now());
        userService.updateById(user);
    }

    private LoginResponse buildLoginResponse(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUserName());
        claims.put("tenantId", user.getTenantCode());
        claims.put("roles", getUserRoles(user.getId()));

        String token = jwtUtil.generateToken(claims, 86400);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUserName(user.getUserName());
        response.setExpiresIn(86400);
        return response;
    }
}
