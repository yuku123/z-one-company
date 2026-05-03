package com.zifang.z.agent.bot.providers;

public class TerminalUI {
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String CYAN = "\u001B[36m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BOLD = "\u001B[1m";

    public static void logo() {
        System.out.println(BOLD + CYAN + "=== zifangBot Local AI ===" + RESET);
        System.out.println(YELLOW + "Model: ollama qwen3:8b" + RESET);
        System.out.println("exit 退出 | clear 清空记忆\n");
    }

    public static void you() {
        System.out.print(GREEN + "主人 > " + RESET);
    }

    public static void ai(String text) {
        System.out.println(CYAN + "AI  > " + RESET + text + "\n");
    }
}