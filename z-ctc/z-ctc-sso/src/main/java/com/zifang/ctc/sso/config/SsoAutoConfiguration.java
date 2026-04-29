package com.zifang.ctc.sso.config;


import com.zifang.ctc.sso.JwtUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 */
@Configuration
@EnableConfigurationProperties(SsoProperties.class)
@ComponentScan("com.zifang.ctc.sso")
public class SsoAutoConfiguration implements WebMvcConfigurer {

    @Bean
    @ConditionalOnMissingBean
    public TokenService tokenService(SsoProperties ssoProperties, RestTemplate restTemplate) {
        return new RemoteTokenService(ssoProperties, restTemplate);
    }

    @Bean
    public SsoInterceptor ssoInterceptor(TokenService tokenService, SsoProperties ssoProperties) {
        return new SsoInterceptor(tokenService, ssoProperties);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ssoInterceptor(tokenService(ssoProperties(), restTemplate()),ssoProperties()))
                .addPathPatterns("/**"); // 拦截所有路径，具体由内部判断
    }

    @Bean
    public SsoProperties ssoProperties() {
        return new SsoProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    @Bean
    @ConditionalOnMissingBean
    public JwtUtil jwtUtil() {
        return  new JwtUtil("ctc-secret-key-2024-secure-jwt-signing-key");
    }

}
