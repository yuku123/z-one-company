package com.zifang.z.mist.starter;

import com.zifang.z.mist.client.config.MistClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mist 自动配置类
 */
@Configuration
@EnableConfigurationProperties(MistProperties.class)
@ConditionalOnProperty(prefix = "z-mist", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MistAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MistClient mistClient(MistProperties properties) {
        MistClient client = new MistClient(
                properties.getServerHost(),
                properties.getServerPort(),
                properties.getAppName(),
                properties.getAppSecret()
        );
        client.start();
        return client;
    }
}