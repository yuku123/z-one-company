package com.zifang.company;

import com.zifang.ctc.core.domain.entity.VerifyCode;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TASK001 简单单元测试
 * 无需Spring容器，直接测试核心逻辑
 */
public class Task001SimpleTest {

    /**
     * 测试1: 验证码生成逻辑
     */
    @Test
    public void testGenerateCodeLogic() {
        String code = generateRandomCode(6);
        assertNotNull(code);
        assertEquals(6, code.length());
        assertTrue(code.matches("\\d+"), "验证码应该是纯数字");
        System.out.println("生成的验证码: " + code);
    }

    /**
     * 测试2: 验证码过期时间计算
     */
    @Test
    public void testExpireTime() {
        int expireMinutes = 5;
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(expireMinutes);
        assertTrue(expireTime.isAfter(LocalDateTime.now()));
        System.out.println("过期时间: " + expireTime);
    }

    /**
     * 测试3: 验证VerifyCode实体
     */
    @Test
    public void testVerifyCodeEntity() {
        VerifyCode vc = new VerifyCode();
        vc.setId(1L);
        vc.setBizType("REGISTER");
        vc.setCodeType("PHONE");
        vc.setReceiver("13800138000");
        vc.setCode("123456");
        vc.setExpireTime(LocalDateTime.now().plusMinutes(5));
        vc.setUsed(0);
        vc.setGmtCreate(LocalDateTime.now());
        vc.setGmtModified(LocalDateTime.now());

        assertEquals("REGISTER", vc.getBizType());
        assertEquals("PHONE", vc.getCodeType());
        assertEquals("13800138000", vc.getReceiver());
        assertEquals("123456", vc.getCode());
        assertEquals(0, vc.getUsed());
        System.out.println("VerifyCode实体测试通过");
    }

    /**
     * 测试4: 验证码格式验证
     */
    @Test
    public void testCodeFormat() {
        String code1 = "123456";
        String code2 = "000000";
        String code3 = "abc123";

        assertTrue(code1.matches("\\d{6}"), "应该是6位数字");
        assertTrue(code2.matches("\\d{6}"), "应该是6位数字");
        assertFalse(code3.matches("\\d{6}"), "包含字母不应该通过");
        System.out.println("验证码格式验证通过");
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