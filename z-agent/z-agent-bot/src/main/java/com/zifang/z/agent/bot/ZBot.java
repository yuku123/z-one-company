package com.zifang.z.agent.bot;

import com.zifang.z.agent.bot.bot.TerminalBot;
import com.zifang.z.agent.bot.providers.TerminalUI;
import com.zifang.z.agent.core.memory.Memory;
import com.zifang.z.agent.core.model.LlmCallerConfig;
import com.zifang.z.agent.core.model.LlmCallerFactory;
import com.zifang.z.agent.core.model.define.Model;
import com.zifang.z.agent.core.model.define.ModelMessage;
import org.apache.commons.lang3.StringUtils;

import java.util.Scanner;

public class ZBot {

    private static Thread thinkingThread;
    private static volatile boolean isThinking = false;

    public static void main(String[] args) {

        TerminalBot terminalBot = new TerminalBot();

        Scanner sc = new Scanner(System.in);

        Memory memory = new Memory();

        Model ai = LlmCallerFactory.create(
                LlmCallerFactory.LLM_CALLER_TYPE_OLLAMA,
                LlmCallerConfig.builder().modelName("qwen2.5:7b-instruct-q4_K_M").build()
        );

        TerminalUI.logo();

        while (true) {
            TerminalUI.you();
            String input = sc.nextLine().trim();

            if ("exit".equalsIgnoreCase(input)) {
                stopThinkingAnimation(); // 退出前停止动画
                System.out.println("Bye~");
                break;
            }
            if ("clear".equalsIgnoreCase(input)) {
                stopThinkingAnimation(); // 清空记忆前停止动画
                memory.clear();
                System.out.println("记忆已清空\n");
                continue;
            }


            if (StringUtils.isEmpty(input)) {
                continue;
            }

            // 加入用户消息
            memory.add(new ModelMessage("user", input));

            try {
                // 启动Thinking动画
                startThinkingAnimation();

                // 调用本地模型
                String reply = ai.chat(memory.getHistory());

                // 停止动画并清空该行
                stopThinkingAnimation();
                clearThinkingLine();

                // 加入AI消息并输出
                memory.add(new ModelMessage("assistant", reply));

                TerminalUI.ai(reply);
            } catch (Exception e) {
                // 异常时也停止动画
                stopThinkingAnimation();
                clearThinkingLine();
                System.out.println("错误：" + e.getMessage() + "\n");
            }
        }
        sc.close();
    }

    /**
     * 启动Thinking动画
     */
    private static void startThinkingAnimation() {
        isThinking = true;
        thinkingThread = new Thread(() -> {
            // nano-boot风格的加载字符
            char[] thinkingChars = {'|', '/', '-', '\\'};
            int index = 0;
            while (isThinking) {
                try {
                    // 输出Thinking动画（不换行）
                    System.out.print("\rAI  > Thinking " + thinkingChars[index++ % thinkingChars.length]);
                    Thread.sleep(100); // 动画速度
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        thinkingThread.start();
    }

    /**
     * 停止Thinking动画
     */
    private static void stopThinkingAnimation() {
        isThinking = false;
        if (thinkingThread != null && thinkingThread.isAlive()) {
            thinkingThread.interrupt();
            try {
                thinkingThread.join(100); // 等待线程停止
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 清空Thinking动画行
     */
    private static void clearThinkingLine() {
        // 清除当前行的Thinking文字
        System.out.print("\r" + StringUtils.repeat(" ", 20) + "\r");
    }
}