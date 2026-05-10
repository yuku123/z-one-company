package com.zifang.z.agent.skill.core.service;

import java.io.IOException;
import java.io.InputStream;

/**
 * 技能包管理 — zip 包的上传存储与下载
 */
public interface SkillPackageBizService {

    /**
     * 存储 zip 包到服务器
     * @param skillCode 技能编码
     * @param version   版本号
     * @param zipStream zip 文件流
     * @return 存储路径（存在 DB 中）
     */
    String storePackage(String skillCode, String version, InputStream zipStream) throws IOException;

    /**
     * 获取 zip 包的路径
     * @param packagePath DB 中存储的路径
     * @return zip 文件路径
     */
    String getPackageFilePath(String packagePath);
}
