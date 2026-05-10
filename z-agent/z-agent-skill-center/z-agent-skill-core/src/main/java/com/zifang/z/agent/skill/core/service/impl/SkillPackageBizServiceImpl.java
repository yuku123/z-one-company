package com.zifang.z.agent.skill.core.service.impl;

import com.zifang.z.agent.skill.core.service.SkillPackageBizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class SkillPackageBizServiceImpl implements SkillPackageBizService {

    private static final Logger log = LoggerFactory.getLogger(SkillPackageBizServiceImpl.class);

    /** zip 包存储根目录 */
    private static final String STORAGE_ROOT = "/tmp/skills";

    @Override
    public String storePackage(String skillCode, String version, InputStream zipStream) throws IOException {
        Path dir = Paths.get(STORAGE_ROOT, skillCode);
        Files.createDirectories(dir);

        String fileName = version + ".zip";
        Path target = dir.resolve(fileName);
        Files.copy(zipStream, target, StandardCopyOption.REPLACE_EXISTING);

        String pathStr = target.toString();
        log.info("Stored skill package: {} v{} → {}", skillCode, version, pathStr);
        return pathStr;
    }

    @Override
    public String getPackageFilePath(String packagePath) {
        return packagePath;
    }
}
