# 运行阶段 - Nginx + Java
FROM nginx:alpine

# 安装 OpenJDK 8 和必要的工具
RUN apk add --no-cache openjdk8-jre curl tzdata \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone \
    && apk del tzdata

# 设置 Java 环境变量
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk
ENV PATH=$PATH:$JAVA_HOME/bin

# 复制 Nginx 配置
COPY nginx.conf /etc/nginx/nginx.conf
COPY default.conf /etc/nginx/conf.d/default.conf

# 复制前端构建产物到 Nginx 目录（已预构建）
COPY ../_frontend/dist /usr/share/nginx/html

# 复制后端构建产物（已预构建）
COPY ../z-schedule-admin/target/z-schedule-admin-1.0-SNAPSHOT.jar /app/z-schedule-admin.jar

# 复制启动脚本
COPY start.sh /start.sh
RUN chmod +x /start.sh

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/z-schedule-admin/actuator/health || exit 1

# 暴露端口
EXPOSE 80 8080

# 启动命令
CMD ["/start.sh"]