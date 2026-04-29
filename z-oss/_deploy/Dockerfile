# 基础镜像：Eclipse Temurin 8 JDK
FROM eclipse-temurin:8-jdk

# 维护者信息
LABEL maintainer="zifang"

# 设置时区
RUN apt-get update && apt-get install -y tzdata && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apt-get remove -y tzdata && apt-get autoremove -y && apt-get clean

# 工作目录
WORKDIR /app

# 创建OSS存储目录
RUN mkdir -p /data/oss

# 复制SpringBoot jar包
COPY ../z-oss-api/target/z-oss-api-1.0.0-SNAPSHOT.jar app.jar

# 暴露端口：HTTP 8088
EXPOSE 8088

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]