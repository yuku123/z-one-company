# 使用本地已存在的镜像进行构建
FROM eclipse-temurin:8-jre

WORKDIR /app

# 安装 nginx 和 curl (用于健康检查)
RUN apt-get update && apt-get install -y nginx curl && rm -rf /var/lib/apt/lists/*

# 复制前端构建产物到 nginx 默认目录
COPY z-one-company-frontend/dist /var/www/html
# 复制后端 jar 包
COPY bootstraps/z-one-company-main-starter/target/z-one-company-main-starter-1.0.0-SNAPSHOT.jar /app/app.jar
# 复制 nginx 配置
COPY _build/nginx.conf /etc/nginx/http.d/default.conf

# 删除默认站点配置
RUN rm -f /etc/nginx/sites-enabled/default

EXPOSE 80 8888

# 启动脚本 - 分别启动 nginx 和 java
COPY _build/start.sh /start.sh
RUN chmod +x /start.sh

CMD ["/start.sh"]