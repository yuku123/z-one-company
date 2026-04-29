package com.zifang.z.config.starter;

import com.zifang.z.config.client.config.ZConfigFactory;
import com.zifang.z.config.client.config.ZConfigService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ZConfig Spring Boot 自动配置类
 */
@Configuration
@ConditionalOnClass(ZConfigService.class) // 当类路径下存在 ZConfigClient 时生效
@EnableConfigurationProperties(ZConfigProperties.class) // 启用配置属性绑定
public class ZConfigAutoConfiguration {

    /**
     * 配置 ZConfig 客户端 Bean
     */
    @Bean
    @ConditionalOnMissingBean // 允许用户自定义客户端实现
    public ZConfigService zConfigClient(ZConfigProperties properties) {
        // 根据配置属性初始化 z-config 客户端
        return ZConfigFactory.createConfigService(properties.asProperties());
    }

    /**
     * 注册 ZConfig Bean 后置处理器
     */
    @Bean
    public ZConfigBeanPostProcessor zConfigBeanPostProcessor(ZConfigService zConfigClient) {
        return new ZConfigBeanPostProcessor(zConfigClient);
    }
}