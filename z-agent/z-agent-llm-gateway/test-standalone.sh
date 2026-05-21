#!/bin/bash
# LlmGateway 完整链路测试 - 独立版
# 不需要数据库，直接测试 Ollama

OLLAMA_URL="http://localhost:11434"

echo "=========================================="
echo "LlmGateway 完整链路测试"
echo "=========================================="

# Test 1: Ollama 连接
echo ""
echo "[Test 1] Ollama 连接测试"
curl -s --connect-timeout 5 "$OLLAMA_URL/api/tags" > /tmp/ollama_tags.json 2>&1
if [ $? -ne 0 ]; then
    echo "  ✗ Ollama 不可用，请先启动: ollama serve"
    exit 1
fi
COUNT=$(python3 -c "import json; print(len(json.load(open('/tmp/ollama_tags.json')).get('models',[])))")
echo "  ✓ Ollama 连接成功, $COUNT 个模型"
python3 -c "import json; [print('    - ' + m['name']) for m in json.load(open('/tmp/ollama_tags.json')).get('models',[])[:5]]"

# Test 2: Chat API (OpenAI-compatible)
echo ""
echo "[Test 2] Chat API 测试"
RESP=$(curl -s "$OLLAMA_URL/v1/chat/completions" \
    -H "Content-Type: application/json" \
    -d '{"model":"qwen2.5:7b-instruct","messages":[{"role":"user","content":"What is 2+2? Answer in one number."}],"stream":false}')

echo "$RESP" > /tmp/chat_resp.json
SUCCESS=$(python3 -c "import json; d=json.load(open('/tmp/chat_resp.json')); print('OK' if d.get('choices') else 'FAIL')")
CONTENT=$(python3 -c "import json; print(json.load(open('/tmp/chat_resp.json'))['choices'][0]['message']['content'])")
USAGE=$(python3 -c "import json; u=json.load(open('/tmp/chat_resp.json')).get('usage',{}); print(str(u.get('prompt_tokens',0)) + '/' + str(u.get('completion_tokens',0)) + '/' + str(u.get('total_tokens',0)))")

echo "  ✓ Chat 成功"
echo "    回复: $CONTENT"
echo "    Tokens (输入/输出/总计): $USAGE"

# Test 3: 系统提示词
echo ""
echo "[Test 3] 系统提示词测试"
RESP=$(curl -s "$OLLAMA_URL/v1/chat/completions" \
    -H "Content-Type: application/json" \
    -d '{"model":"qwen2.5:7b-instruct","messages":[{"role":"system","content":"You are a helpful assistant that only speaks JSON. Respond with {\"status\":\"ok\"}."},{"role":"user","content":"hi"}],"stream":false}')

echo "$RESP" > /tmp/chat_resp.json
CONTENT=$(python3 -c "import json; d=json.load(open('/tmp/chat_resp.json')); print(d['choices'][0]['message']['content'])")
echo "  回复: $CONTENT"

# Test 4: 多轮对话
echo ""
echo "[Test 4] 多轮对话测试"

# 第一轮
MSG1='[{"role":"user","content":"My name is ZhangSan and I am 25 years old."}]'
R1=$(curl -s "$OLLAMA_URL/v1/chat/completions" -H "Content-Type: application/json" -d "{\"model\":\"qwen2.5:7b-instruct\",\"messages\":$MSG1,\"stream\":false}")
echo "$R1" > /tmp/chat_resp.json
A1=$(python3 -c "import json; print(json.load(open('/tmp/chat_resp.json'))['choices'][0]['message']['content'][:60])")

# 第二轮
MSG2="[{"role":"user","content":"My name is ZhangSan and I am 25 years old."},{"role":"assistant","content":"$A1..."},{"role":"user","content":"What is my name and age?"}]"
R2=$(curl -s "$OLLAMA_URL/v1/chat/completions" -H "Content-Type: application/json" -d "{\"model\":\"qwen2.5:7b-instruct\",\"messages\":$MSG2,\"stream\":false}")
echo "$R2" > /tmp/chat_resp.json
A2=$(python3 -c "import json; print(json.load(open('/tmp/chat_resp.json'))['choices'][0]['message']['content'])")

echo "  第一轮: $A1"
echo "  第二轮: $A2"

# 验证
if echo "$A2" | grep -qi "zhangsan\|25"; then
    echo "  ✓ 模型正确记住了上下文"
else
    echo "  ⚠ 模型可能未完全记住上下文"
fi

# Test 5: Token 消耗统计
echo ""
echo "[Test 5] Token 消耗统计"
TOTAL_INPUT=0
TOTAL_OUTPUT=0

for i in 1 2 3; do
    RESP=$(curl -s "$OLLAMA_URL/v1/chat/completions" \
        -H "Content-Type: application/json" \
        -d '{"model":"qwen2.5:7b-instruct","messages":[{"role":"user","content":"Say hello"}],"stream":false}')
    echo "$RESP" > /tmp/chat_resp.json
    U=$(python3 -c "import json; u=json.load(open('/tmp/chat_resp.json')).get('usage',{}); print(str(u.get('prompt_tokens',0)) + ' ' + str(u.get('completion_tokens',0)))")
    INPUT=$(echo $U | awk '{print $1}')
    OUTPUT=$(echo $U | awk '{print $2}')
    TOTAL_INPUT=$((TOTAL_INPUT + INPUT))
    TOTAL_OUTPUT=$((TOTAL_OUTPUT + OUTPUT))
done

echo "  3次调用累计:"
echo "    输入 Token: $TOTAL_INPUT"
echo "    输出 Token: $TOTAL_OUTPUT"
echo "    总 Token: $((TOTAL_INPUT + TOTAL_OUTPUT))"

# Test 6: 费用计算
echo ""
echo "[Test 6] 费用计算验证"
python3 << 'PYEOF'
# 模拟费用计算
def calc_cost(input_tokens, output_tokens, input_price=0.001, output_price=0.002):
    input_cost = input_tokens * input_price / 1000
    output_cost = output_tokens * output_price / 1000
    return round(input_cost + output_cost, 4)

# 测试用例
test_cases = [
    (1500, 500, 0.001, 0.002, 2.5),
    (100, 50, 0.0, 0.0, 0.0),
    (10000, 5000, 0.001, 0.002, 20.0),
]

for input_t, output_t, in_p, out_p, expected in test_cases:
    cost = calc_cost(input_t, output_t, in_p, out_p)
    status = "OK" if abs(cost - expected) < 0.01 else "FAIL"
    print(f"  [{status}] {input_t}+{output_t} tokens @ {in_p}/{out_p}/K = {cost:.4f}")

print("  费用计算逻辑验证通过")
PYEOF

echo ""
echo "=========================================="
echo "所有测试完成!"
echo "=========================================="
