package com.zifang.z.oss.core.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 文件存储配置属性
 */
@ConfigurationProperties(prefix = "oss.storage.file")
public class FileStorageProperties {

    private String root = "/data/oss";

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }
}