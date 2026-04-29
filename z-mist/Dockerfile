# 基础镜像
FROM eclipse-temurin:8-jdk

LABEL maintainer="zifang"

# 设置时区
RUN apt-get update && apt-get install -y tzdata && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apt-get remove -y tzdata && apt-get autoremove -y && apt-get clean

WORKDIR /app

COPY z-mist-admin/target/z-mist-admin-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]