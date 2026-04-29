#!/bin/bash
set -e

# 先以 root 启动 nginx (前台运行)
nginx -g 'daemon off;' &
NGINX_PID=$!

# 等待 nginx 启动
sleep 3

# 启动 java 应用
exec java -jar /app/app.jar \
    --server.port=8888 \
    --spring.datasource.url='jdbc:mysql://101.37.80.51:3306/biz_service?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8' \
    --spring.datasource.username=zifang \
    --spring.datasource.password='Hhzemol!123'