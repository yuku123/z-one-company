#!/bin/bash
set -e

# 配置
PROJECT_NAME="z-one-company"
IMAGE_NAME="${PROJECT_NAME}:latest"
STARTER_MODULE="z-one-company-starter"

# 日志函数
log() {
    echo -e "\033[32m[$(date +'%Y-%m-%d %H:%M:%S')] $1\033[0m"
}

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

log "===================== 构建 ${PROJECT_NAME} Docker 镜像 ====================="

cd ${PROJECT_ROOT}

# 1. Maven 打包
log "步骤1: Maven 打包..."
mvn clean package -DskipTests -pl ${STARTER_MODULE} -am

if [ ! -f "${STARTER_MODULE}/target/${STARTER_MODULE}-1.0.0-SNAPSHOT.jar" ]; then
    log "错误：Maven 打包失败，未找到 jar 文件"
    exit 1
fi

# 2. 构建 Docker 镜像
log "步骤2: 构建 Docker 镜像..."
docker build -t ${IMAGE_NAME} -f docker/Dockerfile .

log "===================== 构建完成 ====================="
log "镜像名称：${IMAGE_NAME}"
log ""
log "启动服务：./docker/start.sh"
log "查看镜像：docker images | grep ${PROJECT_NAME}"