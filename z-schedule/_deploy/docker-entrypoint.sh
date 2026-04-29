#!/bin/bash
# Docker 入口脚本 - 用于动态配置

set -e

# 默认配置
export SERVER_PORT=${SERVER_PORT:-8080}
export DB_HOST=${DB_HOST:-mysql}
export DB_PORT=${DB_PORT:-3306}
export DB_NAME=${DB_NAME:-z_job}
export DB_USERNAME=${DB_USERNAME:-root}
export DB_PASSWORD=${DB_PASSWORD:-root123}

# 生成 application.yml 配置文件
cat > /tmp/application-override.yml << EOF
server:
  port: ${SERVER_PORT}

spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useUnicode=true\&characterEncoding=UTF-8\&autoReconnect=true\&serverTimezone=Asia/Shanghai\&useSSL=false
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    druid:
      initial-size: 5
      min-idle: 5
      max-active: 20
      max-wait: 60000
      time-between-eviction-runs-millis: 60000
      min-evictable-idle-time-millis: 300000
      validation-query: SELECT 1
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      pool-prepared-statements: true
      max-pool-prepared-statement-per-connection-size: 20
      filters: stat,wall,slf4j
      connection-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000

# 日志配置
logging:
  level:
    root: info
    com.zifang.z.job: debug
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n"
  file:
    name: /var/log/z-schedule/admin/z-schedule-admin.log

# 健康检查
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
EOF

# 更新 Nginx 配置中的后端地址
export BACKEND_URL=${BACKEND_URL:-http://localhost:8080}
sed -i "s|proxy_pass http://127.0.0.1:8080|proxy_pass ${BACKEND_URL}|g" /etc/nginx/conf.d/default.conf

echo "=========================================="
echo "  Z-Job 分布式任务调度平台"
echo "=========================================="
echo ""
echo "配置信息:"
echo "  服务器端口: ${SERVER_PORT}"
echo "  数据库地址: ${DB_HOST}:${DB_PORT}"
echo "  数据库名称: ${DB_NAME}"
echo "  后端地址: ${BACKEND_URL}"
echo ""
echo "=========================================="
echo ""

# 启动 Nginx
echo "[1/3] 启动 Nginx..."
nginx -t && nginx
if [ $? -ne 0 ]; then
    echo "错误: Nginx 启动失败"
    exit 1
fi
echo "  ✓ Nginx 启动成功"
echo ""

# 启动 Java 后端
echo "[2/3] 启动后端服务..."

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

# 启动 Java 应用
nohup java $JAVA_OPTS \
    -jar /app/z-schedule-admin.jar \
    --spring.config.location=classpath:/application.yml,/tmp/application-override.yml \
    > /var/log/z-schedule/backend.log 2>&1 &

JAVA_PID=$!
echo "  后端进程 PID: $JAVA_PID"

# 等待后端启动
echo "  等待后端服务启动..."
for i in $(seq 1 120); do
    if curl -s http://localhost:${SERVER_PORT}/z-schedule-admin/actuator/health > /dev/null 2>&1; then
        echo "  ✓ 后端服务启动成功"
        break
    fi

    # 检查进程是否还在运行
    if ! kill -0 $JAVA_PID 2>/dev/null; then
        echo "  ✗ 后端进程异常退出"
        cat /var/log/z-schedule/backend.log
        exit 1
    fi

    if [ $i -eq 120 ]; then
        echo "  ✗ 后端服务启动超时"
        cat /var/log/z-schedule/backend.log
        exit 1
    fi

    echo -n "."
    sleep 1
done
echo ""

# 完成启动
echo "[3/3] 服务启动完成！"
echo ""
echo "========================================="
echo "  Z-Job 分布式任务调度平台已启动"
echo "========================================="
echo ""
echo "访问地址:"
echo "  前端界面: http://localhost"
echo "  后端API: http://localhost/api"
echo "  健康检查: http://localhost/health"
echo ""
echo "日志文件:"
echo "  - 后端日志: /var/log/z-schedule/backend.log"
echo "  - Nginx访问日志: /var/log/nginx/access.log"
echo "  - Nginx错误日志: /var/log/nginx/error.log"
echo ""
echo "常用命令:"
echo "  查看后端日志: docker logs -f <container_id>"
echo "  进入容器: docker exec -it <container_id> sh"
echo ""
echo "========================================="
echo ""

# 保持容器运行
wait
