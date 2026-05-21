# LlmGateway 验证指南

## 1. 前置条件

### 1.1 启动 Ollama
\`\`\`bash
# 检查 Ollama 状态
curl http://localhost:11434/api/tags

# 如果未启动
brew services start ollama  # macOS
# 或
ollama serve
\`\`\`

### 1.2 初始化数据库
\`\`\`bash
# 执行 SQL 初始化脚本
mysql -h 101.37.80.51 -u bwg_root -pbwg_root_pw_123! biz_service < _sql/z-agent-all.sql
\`\`\`

---

## 2. 快速验证（无需启动服务）

### 2.1 Ollama 连通性测试
\`\`\`bash
cd z-agent-llm-gateway
./test-ollama.sh
\`\`\`

预期输出：
- Ollama 连接成功
- 6 个模型可用
- Chat 测试通过
- 多轮对话能记住上下文

---

## 3. 完整链路测试（需要启动服务）

### 3.1 启动服务
\`\`\`bash
cd bootstraps/z-one-company-main-starter
mvn spring-boot:run
\`\`\`

### 3.2 完整链路测试脚本
\`\`\`bash
cd z-agent-llm-gateway
./test-llm-gateway.sh http://localhost:8080
\`\`\`

---

## 4. API 验证清单

### 4.1 LLM Gateway API

| API | 方法 | 说明 | 验证 |
|-----|------|------|------|
| `/api/llm-gateway/chat` | POST | 统一Chat入口 | 调用后检查 `z_llm_usage_record` 表 |
| `/api/llm-gateway/usage/overview` | GET | 用量概览 | 返回累计/今日数据 |
| `/api/llm-gateway/usage/by-app` | GET | 按应用统计 | 返回各应用用量 |
| `/api/llm-gateway/usage/by-user` | GET | 按用户统计 | 返回各用户用量 |
| `/api/llm-gateway/usage/trend` | GET | 用量趋势 | 返回日趋势数据 |
| `/api/llm-gateway/config/provider` | POST | 配置Provider | 动态添加Provider |

### 4.2 Chat 请求示例
\`\`\`bash
curl -X POST http://localhost:8080/api/llm-gateway/chat \
  -H "Content-Type: application/json" \
  -d '{
    "appCode": "my-app",
    "instanceCode": "instance-001",
    "userId": "user-001",
    "userName": "张三",
    "conversationCode": "conv-001",
    "providerCode": "ollama",
    "modelCode": "qwen2.5:7b-instruct",
    "inputPrice": 0.001,
    "outputPrice": 0.002,
    "messages": [
      {"role": "user", "content": "你好"}
    ]
  }'
\`\`\`

### 4.3 验证用量记录
\`\`\`bash
mysql -h 101.37.80.51 -u bwg_root -pbwg_root_pw_123! biz_service -e \
  "SELECT app_code, user_id, model_code, input_tokens, output_tokens, total_tokens, total_cost, status, gmt_create \
   FROM z_llm_usage_record ORDER BY gmt_create DESC LIMIT 10;"
\`\`\`

---

## 5. 数据库表验证

### 5.1 检查表是否存在
\`\`\`sql
SHOW TABLES LIKE 'z_llm%';
\`\`\`

预期结果：
- z_llm_usage_record
- z_llm_usage_daily
- z_llm_usage_conversation
- z_llm_provider
- z_llm_model
- z_agent_tool_template

### 5.2 检查用量汇总
\`\`\`sql
-- 日汇总
SELECT stat_date, app_code, total_calls, total_tokens, total_cost
FROM z_llm_usage_daily
ORDER BY stat_date DESC LIMIT 10;

-- 会话汇总
SELECT conversation_code, app_code, total_calls, total_tokens, total_cost
FROM z_llm_usage_conversation
ORDER BY gmt_create DESC LIMIT 10;
\`\`\`

---

## 6. 常见问题

### 6.1 Ollama 返回 404
确保使用 `/v1/chat/completions` 而非 `/api/chat`

### 6.2 Token 计算为 0
Ollama 本地模型可能不返回完整 usage 信息，不影响功能

### 6.3 费用计算
本地模型 inputPrice/outputPrice 设置为 0 即可
