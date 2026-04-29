#!/bin/bash

# 部署脚本 - 支持 Docker 和手动启动两种模式
#
# 使用方式:
#   ./rebuild.sh docker    # Docker 部署模式 (前端: http://localhost:8080)
#   ./rebuild.sh manual    # 手动启动模式 (前端: http://localhost:3000)
#
# 手动模式需要在 /etc/hosts 添加:
#   127.0.0.1 one.company.dev

MODE=${1:-docker}

echo "========== 模式: $MODE =========="

# 前端编译
cd "$(dirname "$0")"
echo "========== 编译前端 =========="
npm run build

if [ $? -ne 0 ]; then
    echo "前端编译失败"
    exit 1
fi

if [ "$MODE" = "docker" ]; then
    echo "========== Docker 部署模式 =========="
    echo "前端地址: http://localhost:8080"
    echo "后端API: http://localhost:8888"
    echo ""
    cd ../_build
    docker-compose down
    docker-compose up -d

    echo "========== 部署完成 =========="
    echo "注意: 如果前端无法访问，请使用手动模式"
else
    echo "========== 手动启动模式 =========="
    echo ""
    echo "启动命令:"
    echo "  终端1 - 前端: cd z-one-company-frontend && npm run dev"
    echo "  终端2 - 后端: cd bootstraps/z-one-company-main-starter && mvn spring-boot:run"
    echo ""
    echo "前端地址: http://localhost:3000"
    echo "后端API: http://localhost:8888"
    echo ""
    echo "注意: 需要在 /etc/hosts 添加:"
    echo "  127.0.0.1 one.company.dev"
fi