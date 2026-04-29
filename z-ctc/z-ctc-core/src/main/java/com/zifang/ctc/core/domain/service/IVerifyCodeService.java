package com.zifang.ctc.core.domain.service;

import com.zifang.ctc.core.domain.entity.VerifyCode;

/**
 * 验证码服务接口
 */
public interface IVerifyCodeService {

    /**
     * 生成并保存验证码
     * @param receiver 接收者(手机号/邮箱)
     * @param bizType 业务类型
     * @param codeType 验证码类型
     * @return 生成的验证码
     */
    String generateCode(String receiver, String bizType, String codeType);

    /**
     * 验证验证码
     * @param receiver 接收者
     * @param code 验证码
     * @param bizType 业务类型
     * @return 验证是否通过
     */
    boolean verifyCode(String receiver, String code, String bizType);

    /**
     * 检查发送间隔
     * @param receiver 接收者
     * @return 是否可以发送
     */
    boolean canSend(String receiver);

    /**
     * 标记验证码已使用
     * @param id 验证码ID
     */
    void markUsed(Long id);

    /**
     * 根据ID获取验证码
     * @param id 验证码ID
     * @return 验证码实体
     */
    VerifyCode getById(Long id);
}