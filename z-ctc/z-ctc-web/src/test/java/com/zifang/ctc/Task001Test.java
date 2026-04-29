package com.zifang.ctc;

import com.zifang.ctc.core.domain.entity.User;
import com.zifang.ctc.core.domain.service.IVerifyCodeService;
import com.zifang.ctc.core.service.UserBizService;
import com.zifang.ctc.core.service.model.response.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TASK001 用户注册登录功能测试
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Task001Test {

    @Resource
    private IVerifyCodeService verifyCodeService;

    @Resource
    private UserBizService userBizService;

    private static final String TEST_PHONE = "13800138001";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_USERNAME = "testuser001";
    private static final String TEST_PASSWORD = "Test@123456";

    /**
     * 测试1: 验证码生成
     */
    @Test
    public void testGenerateCode() {
        String code = verifyCodeService.generateCode(TEST_PHONE, "REGISTER", "PHONE");
        assertNotNull(code);
        assertEquals(6, code.length());
        System.out.println("验证码生成: " + code);
    }

    /**
     * 测试2: 验证码验证
     */
    @Test
    public void testVerifyCode() {
        String code = verifyCodeService.generateCode(TEST_PHONE, "REGISTER", "PHONE");
        boolean result = verifyCodeService.verifyCode(TEST_PHONE, code, "REGISTER");
        assertTrue(result);
        System.out.println("验证码验证通过");
    }

    /**
     * 测试3: 验证码错误
     */
    @Test
    public void testInvalidCode() {
        verifyCodeService.generateCode(TEST_PHONE, "REGISTER", "PHONE");
        boolean result = verifyCodeService.verifyCode(TEST_PHONE, "000000", "REGISTER");
        assertFalse(result);
        System.out.println("错误验证码验证通过");
    }

    /**
     * 测试4: 发送间隔限制
     */
    @Test
    public void testCanSend() {
        // 首次可以发送
        boolean first = verifyCodeService.canSend(TEST_PHONE);
        assertTrue(first);

        // 生成验证码后，60秒内不能再发送
        verifyCodeService.generateCode(TEST_PHONE, "REGISTER", "PHONE");
        boolean second = verifyCodeService.canSend(TEST_PHONE);
        assertFalse(second);
        System.out.println("发送间隔限制验证通过");
    }

    /**
     * 测试5: 发送注册验证码 - 手机
     */
    @Test
    public void testSendRegisterCodeByPhone() {
        String code = userBizService.sendRegisterCode(TEST_PHONE, "PHONE");
        assertNotNull(code);
        assertEquals(6, code.length());
        System.out.println("手机验证码: " + code);
    }

    /**
     * 测试6: 用户名密码注册
     */
    @Test
    public void testRegisterByUsername() {
        String username = TEST_USERNAME + System.currentTimeMillis();
        LoginResponse response = userBizService.registerByUsername(username, TEST_PASSWORD);

        assertNotNull(response);
        assertNotNull(response.getToken());
        System.out.println("用户名注册成功, token: " + response.getToken().substring(0, 20) + "...");
    }

    /**
     * 测试7: 用户名密码登录
     */
    @Test
    public void testLoginByUsername() {
        // 先注册
        String username = "logintestuser";
        userBizService.registerByUsername(username, TEST_PASSWORD);

        // 执行登录
        User user = userBizService.authenticate(username, TEST_PASSWORD);

        assertNotNull(user);
        assertEquals(username, user.getUserName());
        System.out.println("用户名登录成功: " + user.getUserName());
    }

    /**
     * 测试8: 手机注册
     */
    @Test
    public void testRegisterByPhone() {
        // 先发送验证码
        String code = userBizService.sendRegisterCode(TEST_PHONE, "PHONE");

        // 执行手机注册
        LoginResponse response = userBizService.registerByPhone(TEST_PHONE, code, TEST_PASSWORD);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertNotNull(response.getUserId());
        System.out.println("手机注册成功, userId: " + response.getUserId());
    }

    /**
     * 测试9: 发送重置密码验证码
     */
    @Test
    public void testSendResetPasswordCode() {
        // 先注册用户
        String phone = "13700137001";
        String code = userBizService.sendRegisterCode(phone, "PHONE");
        userBizService.registerByPhone(phone, code, TEST_PASSWORD);

        // 发送重置密码验证码
        userBizService.sendResetPasswordCode(phone, "PHONE");
        System.out.println("重置密码验证码发送成功");
    }
}