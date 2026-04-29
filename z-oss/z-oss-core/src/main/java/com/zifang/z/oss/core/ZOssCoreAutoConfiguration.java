package com.zifang.z.oss.core;

import com.zifang.z.oss.core.storage.FileStorageEngine;
import com.zifang.z.oss.core.storage.StorageEngine;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Z-OSS Core 自动配置
 */
@Configuration
@MapperScan("com.zifang.z.oss.core.domain.mapper")
public class ZOssCoreAutoConfiguration {

    @Value("${oss.storage.file.root:/data/oss}")
    private String rootPath;

    @Bean
    @ConditionalOnMissingBean
    public StorageEngine storageEngine() {
        System.out.println("=== Creating StorageEngine with rootPath: " + rootPath + " ===");
        FileStorageEngine engine = new FileStorageEngine();
        engine.setRootPath(rootPath);
        return engine;
    }

    @Bean
    @ConditionalOnMissingBean
    public String zOssCoreDummy() {
        return "z-oss-core";
    }
}