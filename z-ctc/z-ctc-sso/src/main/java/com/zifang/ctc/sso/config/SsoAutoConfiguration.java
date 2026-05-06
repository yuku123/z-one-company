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
    public TokenService tokenService(SsoProperties ssoProperties, RestTemplate restTemplate, JwtUtil jwtUtil) {
        return new LocalTokenService(jwtUtil);
    }

    @Bean
    public SsoInterceptor ssoInterceptor(TokenService tokenService, SsoProperties ssoProperties) {
        return new SsoInterceptor(tokenService, ssoProperties);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        SsoInterceptor interceptor = new SsoInterceptor(
                tokenService(ssoProperties(), restTemplate(), jwtUtil()),
                ssoProperties()
        );
        interceptor.setExcludedPaths(java.util.Arrays.asList(
                "/api/auth/login",
                "/api/auth/register",
                "/api/auth/verify",
                "/api/auth/send-code",
                "/api/auth/reset-password",
                "/login", "/doc.html", "/swagger-resources/**",
                "/favicon.ico"
        ));
        interceptor.setInterceptPaths(java.util.Arrays.asList("/api/**"));
        registry.addInterceptor(interceptor).addPathPatterns("/**");
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
