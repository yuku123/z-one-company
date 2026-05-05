package com.zifang.ctc.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.domain.entity.User;
import com.zifang.ctc.core.service.model.request.UserPageReq;
import com.zifang.ctc.core.service.model.request.RegisterRequest;
import com.zifang.ctc.core.service.model.response.LoginResponse;

import java.util.List;

/**
 * 用户业务服务接口
 */
public interface UserBizService {

    /**
     * 用户认证
     */
    User authenticate(String userName, String password);

    /**
     * 用户注册
     */
    void register(RegisterRequest request);

    /**
     * 用户登出
     */
    void logout(String token);

    /**
     * 刷新令牌
     */
    LoginResponse refreshToken(String token);

    /**
     * 根据ID获取用户
     */
    User getById(Long id);

    /**
     * 获取用户列表
     */
    List<User> list();

    /**
     * 分页查询用户
     * @param req 分页请求（含 current/size 和搜索字段）
     * @return 分页结果
     */
    IPage<User> page(UserPageReq req);

    /**
     * 创建用户
     */
    void create(User user);

    /**
     * 更新用户
     */
    void update(User user);

    /**
     * 删除用户
     */
    void delete(Long id);

    /**
     * 获取用户角色列表
     */
    List<String> getUserRoles(Long userId);

    /**
     * 分配角色给用户
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置密码
     */
    void resetPassword(Long userId, String newPassword);

    // ========== TASK001 新增方法 ==========

    /**
     * 发送注册验证码
     * @param receiver 接收者(手机号/邮箱)
     * @param codeType 验证码类型 PHONE/EMAIL
     * @return 验证码(测试用，实际应返回void)
     */
    String sendRegisterCode(String receiver, String codeType);

    /**
     * 手机注册
     * @param phone 手机号
     * @param code 验证码
     * @param password 密码
     * @return 登录响应
     */
    LoginResponse registerByPhone(String phone, String code, String password);

    /**
     * 邮箱注册
     * @param email 邮箱
     * @param code 验证码
     * @param password 密码
     * @return 登录响应
     */
    LoginResponse registerByEmail(String email, String code, String password);

    /**
     * 用户名密码注册(无验证码)
     * @param username 用户名
     * @param password 密码
     * @return 登录响应
     */
    LoginResponse registerByUsername(String username, String password);

    /**
     * 手机登录
     * @param phone 手机号
     * @param code 验证码
     * @return 登录响应
     */
    LoginResponse loginByPhone(String phone, String code);

    /**
     * 发送重置密码验证码
     * @param receiver 接收者(手机号/邮箱)
     * @param codeType 验证码类型
     */
    void sendResetPasswordCode(String receiver, String codeType);

    /**
     * 手机找回密码
     * @param phone 手机号
     * @param code 验证码
     * @param newPassword 新密码
     */
    void resetPasswordByPhone(String phone, String code, String newPassword);

    /**
     * 邮箱找回密码
     * @param email 邮箱
     * @param code 验证码
     * @param newPassword 新密码
     */
    void resetPasswordByEmail(String email, String code, String newPassword);
}
