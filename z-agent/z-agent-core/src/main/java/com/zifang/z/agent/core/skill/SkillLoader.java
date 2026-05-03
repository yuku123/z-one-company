package com.zifang.z.agent.core.skill;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * JDK1.8 兼容 - 加载本地skills文件夹的JSON技能
 */
public class SkillLoader {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Skill> loadSkills(String dirPath) {
        List<Skill> skills = new ArrayList<>();
        File dir = new File(dirPath);

        // JDK1.8 兼容：空判断
        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("Skills目录不存在：" + dirPath);
            return skills;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files == null) {
            return skills;
        }

        for (File file : files) {
            try {
                Skill skill = objectMapper.readValue(file, Skill.class);
                skills.add(skill);
            } catch (Exception e) {
                System.err.println("加载Skill失败：" + file.getName());
                e.printStackTrace();
            }
        }
        return skills;
    }
}