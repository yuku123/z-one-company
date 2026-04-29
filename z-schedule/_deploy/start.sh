#!/bin/sh

# Z-Job 统一启动脚本
# 同时启动 Nginx（前端）和 Java 后端

echo "========================================="
echo "  Z-Job 分布式任务调度平台"
echo "========================================="
echo ""

# 设置时区
export TZ=Asia/Shanghai

# 创建日志目录
mkdir -p /var/log/z-schedule

# 检查必要的文件
echo "[1/4] 检查环境..."

if [ ! -f "/app/z-schedule-admin.jar" ]; then
    echo "错误: 后端 JAR 文件不存在"
    exit 1
fi

if [ ! -d "/usr/share/nginx/html" ]; then
    echo "错误: 前端静态文件不存在"
    exit 1
fi

echo "  ✓ 环境检查通过"
echo ""

# 启动 Java 后端
echo "[2/4] 启动后端服务..."

# JVM 参数
JAVA_OPTS="-server \
    -Xms512m \
    -Xmx512m \
    -XX:MetaspaceSize=128m \
    -XX:MaxMetaspaceSize=256m \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/tmp/heapdump.hprof \
    -Djava.security.egd=file:/dev/./urandom \
    -Dfile.encoding=UTF-8 \
    -Duser.timezone=Asia/Shanghai"

# 启动后端服务
nohup java $JAVA_OPTS -jar /app/z-schedule-admin.jar \
    --server.port=8080 \
    --logging.file.path=/var/log/z-schedule \
    > /var/log/z-schedule/backend.log 2>&1 &

BACKEND_PID=$!
echo "  后端进程 PID: $BACKEND_PID"

# 等待后端启动
echo "  等待后端服务启动..."
for i in $(seq 1 60); do
    if curl -s http://localhost:8080/z-schedule-admin/actuator/health > /dev/null 2>&1; then
        echo "  ✓ 后端服务启动成功"
        break
    fi
    if [ $i -eq 60 ]; then
        echo "  ✗ 后端服务启动超时，请检查日志"
        cat /var/log/z-schedule/backend.log
        exit 1
    fi
    sleep 1
done
echo ""

# 启动 Nginx
echo "[3/4] 启动前端服务..."
nginx -t > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "  ✗ Nginx 配置测试失败"
    exit 1
fi

nginx
if [ $? -ne 0 ]; then
    echo "  ✗ Nginx 启动失败"
    exit 1
fi
echo "  ✓ 前端服务启动成功"
echo ""

# 完成启动
echo "[4/4] 服务启动完成！"
echo ""
echo "========================================="
echo "  前端访问: http://localhost"
echo "  后端API: http://localhost/api"
echo "  健康检查: http://localhost/health"
echo "========================================="
echo ""
echo "日志文件:"
echo "  - 后端日志: /var/log/z-schedule/backend.log"
echo "  - Nginx访问日志: /var/log/nginx/access.log"
echo "  - Nginx错误日志: /var/log/nginx/error.log"
echo ""
echo "按 Ctrl+C 停止服务，或运行 docker stop 命令"
echo ""

# 保持容器运行
wait
