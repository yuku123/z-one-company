package com.zifang.z.agent.core;

import com.zifang.z.agent.core.model.LlmCallerConfig;
import com.zifang.z.agent.core.model.LlmCallerFactory;
import com.zifang.z.agent.core.model.define.Model;
import com.zifang.z.agent.core.skill.Skill;
import com.zifang.z.agent.core.demo.AgentEngine;
import com.zifang.z.agent.core.demo.LocalModelClient;
import com.zifang.z.agent.core.skill.SkillLoader;

import java.util.List;


/**
 * 1. 执行skills
 * 2. 自定义一个跨终端的agent-team
 */
public class MockExecuteSkills {


    public static void main(String[] args) {
        try {
            SkillLoader skillLoader = new SkillLoader();
            List<Skill> skills = skillLoader.loadSkills("./skills");
            System.out.println("加载到Skill数量：" + skills.size());

            Model model = LlmCallerFactory.create(
                    LlmCallerFactory.LLM_CALLER_TYPE_OLLAMA,
                    LlmCallerConfig.builder().modelName("qwen3:8b").build()
            );

            // 2. 初始化本地模型客户端, 暂时用ollama
            LocalModelClient modelClient = new LocalModelClient("qwen3:8b");

            // 3. 初始化Agent引擎
            AgentEngine agentEngine = new AgentEngine(skills, modelClient, model);

            // 4. 执行用户任务
            String userIntent = "创建 test.py 文件，内容是 打印当前时间，然后运行这个文件，返回文件路径与执行结果";
            agentEngine.runTask2(userIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}