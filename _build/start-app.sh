#!/bin/bash
# 启动 z-one-company 服务

cd "$(dirname "$0")/bootstraps/z-one-company-main-starter"

echo "正在启动 z-one-company 服务..."

nohup mvn -q exec:java -Dexec.mainClass="com.zifang.z.one.company.main.starter.ZCompanyMainStarter" > /tmp/z-one-company.log 2>&1 &

echo "服务已启动，日志: /tmp/z-one-company.log"
echo "等待 10 秒..."
sleep 10

# 检查是否启动成功
if lsof -ti:8888 > /dev/null 2>&1; then
    echo "服务启动成功，访问 http://localhost:8888/doc.html"
else
    echo "服务启动失败，查看日志: /tmp/z-one-company.log"
fi