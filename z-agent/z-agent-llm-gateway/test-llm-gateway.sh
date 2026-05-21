#!/bin/bash
# LlmGateway 链路测试脚本

GATEWAY_URL="${1:-http://localhost:8080}"
OLLAMA_URL="http://localhost:11434"

echo "=========================================="
echo "LlmGateway 完整链路测试"
echo "Gateway: $GATEWAY_URL"
echo "Ollama: $OLLAMA_URL"
echo "=========================================="

# Test 1: Ollama 连接
echo ""
echo "[Test 1] Ollama 连接测试"
curl -s "$OLLAMA_URL/api/tags" | python3 -c "
import sys, json
d = json.load(sys.stdin)
models = d.get('models', [])
print(f'  ✓ Ollama 正常, {len(models)} 个模型')
for m in models[:3]:
    print(f'    - {m["name"]}')" || {
    echo "  ✗ Ollama 不可用"
    exit 1
}

# Test 2: Ollama Chat (原生API)
echo ""
echo "[Test 2] Ollama Chat 测试"
RESP=$(curl -s "$OLLAMA_URL/v1/chat/completions"     -H "Content-Type: application/json"     -d '{"model":"qwen2.5:7b-instruct","messages":[{"role":"user","content":"Say hi in 3 words"}],"stream":false}')
echo "$RESP" | python3 -c "
import sys, json
d = json.load(sys.stdin)
msg = d.get('choices', [{}])[0].get('message', {})
usage = d.get('usage', {})
print(f'  ✓ Chat 成功')
print(f'    回复: {msg.get("content", "N/A")}')
print(f'    Input: {usage.get("prompt_tokens", 0)} tokens')
print(f'    Output: {usage.get("completion_tokens", 0)} tokens')
print(f'    Total: {usage.get("total_tokens", 0)} tokens')"

# Test 3: LlmGateway Chat API
echo ""
echo "[Test 3] LlmGateway Chat API 测试"
CHAT_RESP=$(curl -s -X POST "$GATEWAY_URL/api/llm-gateway/chat"     -H "Content-Type: application/json"     -d '{
        "appCode": "test-app",
        "instanceCode": "test-instance",
        "userId": "user001",
        "userName": "测试用户",
        "conversationCode": "conv-test-001",
        "providerCode": "ollama",
        "modelCode": "qwen2.5:7b-instruct",
        "inputPrice": 0.001,
        "outputPrice": 0.002,
        "messages": [{"role": "user", "content": "What is 2+2?"}]
    }')

echo "$CHAT_RESP" | python3 -c "
import sys, json
d = json.load(sys.stdin)
if d.get('success'):
    print(f'  ✓ Gateway Chat 成功')
    print(f'    回复: {d.get("content", "N/A")[:100]}...')
    print(f'    Input: {d.get("inputTokens", 0)} tokens')
    print(f'    Output: {d.get("outputTokens", 0)} tokens')
    print(f'    Total: {d.get("totalTokens", 0)} tokens')
else:
    print(f'  ✗ Chat 失败: {d.get("error", "Unknown")}')"

# Test 4: 用量概览
echo ""
echo "[Test 4] 用量统计 API 测试"
curl -s "$GATEWAY_URL/api/llm-gateway/usage/overview" | python3 -c "
import sys, json
try:
    d = json.load(sys.stdin)
    data = d.get('data', d)
    print(f'  ✓ 用量统计获取成功')
    print(f'    累计 Tokens: {data.get("totalTokens", 0):,}')
    print(f'    累计 Calls: {data.get("totalCalls", 0):,}')
    print(f'    累计 Cost: ¥{data.get("totalCost", 0):.4f}')
    print(f'    今日 Tokens: {data.get("todayTokens", 0):,}')
except Exception as e:
    print(f'  ⚠ {e}')" 2>/dev/null || echo "  ⚠ Gateway 未启动，跳过"

# Test 5: 按 App 统计
echo ""
echo "[Test 5] 按应用统计"
curl -s "$GATEWAY_URL/api/llm-gateway/usage/by-app" | python3 -c "
import sys, json
try:
    d = json.load(sys.stdin)
    items = d.get('data', d) if isinstance(d, dict) else d
    if items:
        print(f'  ✓ 按应用统计:')
        for item in items[:5]:
            print(f'    {item.get("appCode", "N/A")}: {item.get("totalTokens", 0):,} tokens')
    else:
        print(f'  ⚠ 暂无数据')
except Exception as e:
    print(f'  ⚠ {e}')" 2>/dev/null || echo "  ⚠ Gateway 未启动，跳过"

echo ""
echo "=========================================="
echo "测试完成"
echo "=========================================="
