# 基础镜像：Eclipse Temurin 8 JDK（支持amd64/arm64架构，M系列Mac可用）
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

# 复制SpringBoot jar包
COPY z-meta-admin/target/z-meta-admin-1.0.0-SNAPSHOT.jar app.jar

# 暴露端口：HTTP 8080
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]