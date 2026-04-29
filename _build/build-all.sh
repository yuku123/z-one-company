#!/bin/bash
set -e

PROJECT_NAME="z-one-company"
IMAGE_NAME="${PROJECT_NAME}:latest"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

log() {
    echo -e "\033[32m[$(date +'%Y-%m-%d %H:%M:%S')] $1\033[0m"
}

cd ${PROJECT_ROOT}

log "===================== 构建 ${PROJECT_NAME} Docker 镜像 ====================="

log "步骤1: 检查 Docker..."
if ! docker info >/dev/null 2>&1; then
    log "错误：Docker 未运行"
    exit 1
fi

log "步骤2: 构建 Docker 镜像 (多阶段构建)..."
docker build -t ${IMAGE_NAME} -f _build/Dockerfile .

log "===================== 构建完成 ====================="
log "镜像名称：${IMAGE_NAME}"
log ""
log "启动服务：docker run -d -p 80:80 -p 8888:8888 --name ${PROJECT_NAME} ${IMAGE_NAME}"
log "访问地址：http://localhost"
log "API 文档：http://localhost/doc.html"