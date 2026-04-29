package com.zifang.z.ext.starter;

import com.zifang.z.ext.annotation.EnableExt;
import com.zifang.z.ext.core.proxy.ExtProxyFactory;
import com.zifang.z.ext.core.registry.ExtRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 扩展平台自动配置
 */
@AutoConfiguration
@EnableConfigurationProperties(ExtProperties.class)
@ConditionalOnProperty(prefix = "z-ext", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ExtAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ExtRegistrarBean extRegistrarBean(ExtProperties properties) {
        return new ExtRegistrarBean(properties);
    }

    /**
     * 扩展注册器Bean
     * 负责扫描和注册扩展点
     */
    public static class ExtRegistrarBean {

        private final ExtProperties properties;

        public ExtRegistrarBean(ExtProperties properties) {
            this.properties = properties;
            init();
        }

        private void init() {
            // 这里可以进行初始化工作
            // 例如：加载配置文件中的路由规则
            System.out.println("[Ext] Initializing extension platform with base packages: " +
                    String.join(", ", properties.getBasePackages()));
        }
    }
}