package com.zifang.z.agent.mcp.impl.db;

import com.zifang.z.agent.mcp.starter.McpParam;
import com.zifang.z.agent.mcp.starter.McpTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * DB MCP 工具集 — 通过 @McpTool 暴露为 MCP 工具。
 *
 * 每个方法自动注册为 MCP 工具，可通过:
 *   POST /mcp  {method:"tools/call", params:{name:"list_tables", arguments:{...}}}
 * 调用。
 */
@Service
public class McpDbTools {

    private static final Logger log = LoggerFactory.getLogger(McpDbTools.class);

    private final JdbcTemplate jdbc;

    public McpDbTools(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * 列出数据库中的所有表
     */
    @McpTool(name = "list_tables", description = "列出数据库中的所有表名")
    public String listTables() {
        List<String> tables = jdbc.queryForList("SHOW TABLES", String.class);
        StringBuilder sb = new StringBuilder();
        sb.append("=== Database Tables (").append(tables.size()).append(") ===\n");
        for (int i = 0; i < tables.size(); i++) {
            sb.append(String.format("  %3d. %s\n", i + 1, tables.get(i)));
        }
        if (tables.isEmpty()) {
            sb.append("  (no tables found)\n");
        }
        return sb.toString();
    }

    /**
     * 查询单个表的字段结构
     */
    @McpTool(name = "describe_table", description = "查看表的字段结构（列名、类型、是否可空等）")
    public String describeTable(
            @McpParam(name = "table", description = "表名", required = true) String table
    ) {
        List<Map<String, Object>> columns = jdbc.queryForList("DESCRIBE " + safeName(table));
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(table).append(" (").append(columns.size()).append(" columns) ===\n");
        sb.append(String.format("%-25s %-20s %-6s %-8s %s\n", "Field", "Type", "Null", "Key", "Default"));
        sb.append("-".repeat(90)).append("\n");
        for (Map<String, Object> col : columns) {
            sb.append(String.format("%-25s %-20s %-6s %-8s %s\n",
                    col.get("Field"),
                    col.get("Type"),
                    col.get("Null"),
                    col.get("Key") != null ? col.get("Key") : "",
                    col.get("Default") != null ? col.get("Default") : ""));
        }
        return sb.toString();
    }

    /**
     * 执行 SELECT 查询（只读，限制返回行数）
     */
    @McpTool(name = "execute_query", description = "执行 SELECT 查询（只读，最多返回 50 行）")
    public String executeQuery(
            @McpParam(name = "sql", description = "SELECT 语句", required = true) String sql,
            @McpParam(name = "limit", description = "最大返回行数，默认 20") Integer limit
    ) {
        String trimmed = sql.trim().toUpperCase();
        if (!trimmed.startsWith("SELECT") && !trimmed.startsWith("SHOW") && !trimmed.startsWith("DESCRIBE")) {
            return "ERROR: Only SELECT / SHOW / DESCRIBE queries are allowed.";
        }
        if (trimmed.contains("DROP") || trimmed.contains("DELETE") || trimmed.contains("UPDATE")
                || trimmed.contains("INSERT") || trimmed.contains("ALTER") || trimmed.contains("TRUNCATE")) {
            return "ERROR: DML/DDL statements are forbidden.";
        }

        int maxRows = limit != null ? Math.min(limit, 50) : 20;
        String limitedSql = sql.trim();
        if (!limitedSql.toUpperCase().contains("LIMIT")) {
            limitedSql += " LIMIT " + maxRows;
        }

        try {
            List<Map<String, Object>> rows = jdbc.queryForList(limitedSql);
            if (rows.isEmpty()) {
                return "Query returned 0 rows.";
            }

            // 获取列名
            Set<String> colNames = new LinkedHashSet<>();
            for (Map<String, Object> row : rows) {
                colNames.addAll(row.keySet());
            }

            StringBuilder sb = new StringBuilder();
            sb.append("=== Query Result (").append(rows.size()).append(" rows) ===\n");

            // Header
            for (String col : colNames) {
                sb.append(String.format("%-20s", truncate(col, 18)));
            }
            sb.append("\n").append("-".repeat(colNames.size() * 20)).append("\n");

            // Rows
            for (Map<String, Object> row : rows) {
                for (String col : colNames) {
                    Object val = row.get(col);
                    sb.append(String.format("%-20s", truncate(val != null ? val.toString() : "NULL", 18)));
                }
                sb.append("\n");
            }

            return sb.toString();
        } catch (Exception e) {
            log.error("Query failed: {}", sql, e);
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * 统计表行数
     */
    @McpTool(name = "count_table", description = "统计指定表的行数")
    public String countTable(
            @McpParam(name = "table", description = "表名", required = true) String table
    ) {
        try {
            Long count = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM " + safeName(table), Long.class);
            return "Table '" + table + "' has " + count + " rows.";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * 显示所有数据库（SHOW DATABASES）
     */
    @McpTool(name = "list_databases", description = "列出 MySQL 服务器上的所有数据库")
    public String listDatabases() {
        List<String> dbs = jdbc.queryForList("SHOW DATABASES", String.class);
        StringBuilder sb = new StringBuilder();
        sb.append("=== Databases (").append(dbs.size()).append(") ===\n");
        for (String db : dbs) {
            sb.append("  - ").append(db).append("\n");
        }
        return sb.toString();
    }

    // ─── 辅助方法 ───

    private String safeName(String name) {
        // 简单防注入：只允许字母数字下划线
        return name.replaceAll("[^a-zA-Z0-9_]", "");
    }

    private String truncate(String s, int max) {
        if (s == null) return "NULL";
        return s.length() > max ? s.substring(0, max - 2) + ".." : s;
    }
}
