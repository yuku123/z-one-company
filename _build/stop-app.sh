#!/bin/bash
# 停止 z-one-company 服务

lsof -ti:8888 | xargs kill -9 2>/dev/null
echo "端口 8888 已停止"

# 可选：也停止 8080 端口（如果被占用）
# lsof -ti:8080 | xargs kill -9 2>/dev/null
# echo "端口 8080 已停止"