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
MODELS=$(curl -s "$OLLAMA_URL/api/tags")
COUNT=$(echo "$MODELS" | python3 -c "import sys,json; print(len(json.load(sys.stdin).get('models',[])))")
echo "  ✓ Ollama 正常, $COUNT 个模型"
echo "$MODELS" | python3 -c "import sys,json; [print('    - ' + m['name']) for m in json.load(sys.stdin).get('models',[])[:3]]"

# Test 2: Ollama Chat (OpenAI-compatible API)
echo ""
echo "[Test 2] Ollama Chat 测试"
RESP=$(curl -s "$OLLAMA_URL/v1/chat/completions" \
    -H "Content-Type: application/json" \
    -d '{"model":"qwen2.5:7b-instruct","messages":[{"role":"user","content":"Say hi in 3 words"}],"stream":false}')
CONTENT=$(echo "$RESP" | python3 -c "import sys,json; print(json.load(sys.stdin)['choices'][0]['message']['content'])")
echo "  ✓ Chat 成功: $CONTENT"

# Test 3: 多轮对话
echo ""
echo "[Test 3] 多轮对话测试"
MSG1='[{"role":"user","content":"My name is Alice"}]'
MSG2='[{"role":"user","content":"My name is Alice"},{"role":"assistant","content":"Hello Alice!"},{"role":"user","content":"What is my name?"}]'

R1=$(curl -s "$OLLAMA_URL/v1/chat/completions" -H "Content-Type: application/json" -d "{\"model\":\"qwen2.5:7b-instruct\",\"messages\":$MSG1,\"stream\":false}")
R2=$(curl -s "$OLLAMA_URL/v1/chat/completions" -H "Content-Type: application/json" -d "{\"model\":\"qwen2.5:7b-instruct\",\"messages\":$MSG2,\"stream\":false}")
A2=$(echo "$R2" | python3 -c "import sys,json; print(json.load(sys.stdin)['choices'][0]['message']['content'])")
echo "  第一轮: $(echo "$R1" | python3 -c "import sys,json; print(json.load(sys.stdin)['choices'][0]['message']['content'][:50])")"
echo "  第二轮: $A2"

echo ""
echo "=========================================="
echo "Ollama 链路验证完成"
echo "=========================================="
