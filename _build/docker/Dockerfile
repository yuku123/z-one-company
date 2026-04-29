# 基础镜像：OpenJDK 8 Alpine 精简版
FROM openjdk:8-jre-alpine

# 维护者信息
MAINTAINER zifang

# 设置时区
RUN apk add --no-cache tzdata && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apk del tzdata

# 工作目录
WORKDIR /app

# 复制SpringBoot jar包
COPY z-one-company-starter/target/z-one-company-starter-1.0.0-SNAPSHOT.jar app.jar

# 暴露端口：HTTP 8080
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]