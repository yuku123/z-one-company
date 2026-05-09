package com.zifang.z.agent.mcp.impl.db;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * z-agent-mcp-impl-db — 数据库 MCP 服务
 *
 * 提供以下 MCP 工具：
 *   - list_tables    : 列出所有表
 *   - describe_table : 查看表结构
 *   - execute_query  : 执行只读 SQL
 *   - count_table    : 统计表行数
 *   - list_databases : 列出所有数据库
 *
 * 启动后访问: POST http://localhost:8095/mcp
 */
@SpringBootApplication(scanBasePackages = {
        "com.zifang.z.agent.mcp.impl.db",
        "com.zifang.z.agent.mcp.starter"  // 扫描 starter 的自动配置
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
