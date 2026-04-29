package com.zifang.ctc.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zifang.ctc.core.domain.entity.VerifyCode;
import com.zifang.ctc.core.domain.mapper.VerifyCodeMapper;
import com.zifang.ctc.core.domain.service.IVerifyCodeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * 验证码服务实现
 */
@Service
public class VerifyCodeServiceImpl implements IVerifyCodeService {

    @Resource
    private VerifyCodeMapper verifyCodeMapper;

    /**
     * 验证码长度
     */
    private static final int CODE_LENGTH = 6;

    /**
     * 验证码有效期(分钟)
     */
    private static final int EXPIRE_MINUTES = 5;

    /**
     * 发送间隔(秒)
     */
    private static final int SEND_INTERVAL = 60;

    @Override
    public String generateCode(String receiver, String bizType, String codeType) {
        // 生成6位数字验证码
        String code = generateRandomCode(CODE_LENGTH);

        // 保存验证码记录
        VerifyCode verifyCode = new VerifyCode();
        verifyCode.setReceiver(receiver);
        verifyCode.setBizType(bizType);
        verifyCode.setCodeType(codeType);
        verifyCode.setCode(code);
        verifyCode.setExpireTime(LocalDateTime.now().plusMinutes(EXPIRE_MINUTES));
        verifyCode.setUsed(0);
        verifyCode.setGmtCreate(LocalDateTime.now());
        verifyCode.setGmtModified(LocalDateTime.now());

        verifyCodeMapper.insert(verifyCode);

        return code;
    }

    @Override
    public boolean verifyCode(String receiver, String code, String bizType) {
        LambdaQueryWrapper<VerifyCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VerifyCode::getReceiver, receiver)
                .eq(VerifyCode::getCode, code)
                .eq(VerifyCode::getBizType, bizType)
                .eq(VerifyCode::getUsed, 0)
                .gt(VerifyCode::getExpireTime, LocalDateTime.now())
                .orderByDesc(VerifyCode::getGmtCreate)
                .last("LIMIT 1");

        VerifyCode verifyCode = verifyCodeMapper.selectOne(wrapper);

        if (verifyCode != null) {
            // 标记为已使用
            markUsed(verifyCode.getId());
            return true;
        }

        return false;
    }

    @Override
    public boolean canSend(String receiver) {
        LambdaQueryWrapper<VerifyCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VerifyCode::getReceiver, receiver)
                .gt(VerifyCode::getGmtCreate, LocalDateTime.now().minusSeconds(SEND_INTERVAL))
                .orderByDesc(VerifyCode::getGmtCreate)
                .last("LIMIT 1");

        VerifyCode recentCode = verifyCodeMapper.selectOne(wrapper);

        // 如果最近一次发送在SEND_INTERVAL秒之前，则可以发送
        return recentCode == null;
    }

    @Override
    public void markUsed(Long id) {
        VerifyCode verifyCode = verifyCodeMapper.selectById(id);
        if (verifyCode != null) {
            verifyCode.setUsed(1);
            verifyCode.setGmtModified(LocalDateTime.now());
            verifyCodeMapper.updateById(verifyCode);
        }
    }

    @Override
    public VerifyCode getById(Long id) {
        return verifyCodeMapper.selectById(id);
    }

    /**
     * 生成随机数字验证码
     */
    private String generateRandomCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}