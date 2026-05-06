package com.zifang.z.config.web.config;

import com.zifang.ctc.sso.config.SsoAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * z-config-web 接入 z-ctc-sso-starter
 * SsoInterceptor 拦截所有路径，排除 /api/auth/*（登录注册由 z-ctc 提供）
 */
@Configuration
public class WebMvcConfig extends SsoAutoConfiguration {
}
