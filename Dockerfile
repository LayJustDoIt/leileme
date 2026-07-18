# syntax=docker/dockerfile:1.4
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
# 注入干净的 Maven settings.xml，覆盖全局配置中可能存在的内网镜像（如 wpg-maven-public），
# 确保 Docker 构建环境能正常从 Maven Central 解析依赖。
RUN mkdir -p /root/.m2 && printf '%s\n' \
  '<?xml version="1.0" encoding="UTF-8"?>' \
  '<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"' \
  '          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"' \
  '          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">' \
  '    <mirrors>' \
  '        <mirror>' \
  '            <id>central</id>' \
  '            <name>Maven Central Repository</name>' \
  '            <url>https://repo1.maven.org/maven2/</url>' \
  '            <mirrorOf>*</mirrorOf>' \
  '        </mirror>' \
  '    </mirrors>' \
  '</settings>' > /root/.m2/settings.xml
COPY pom.xml .
# 使用 BuildKit bind mount 复用宿主机 maven 本地仓库缓存，避免 Docker 构建时重新下载依赖
# macOS Docker Desktop 已将 /Users 共享到虚拟机，可直接 bind mount
RUN --mount=type=bind,source=/Users/liushaowu/.m2/repository,target=/root/.m2/repository \
    mvn -q -DskipTests dependency:go-offline || true
COPY src ./src
RUN --mount=type=bind,source=/Users/liushaowu/.m2/repository,target=/root/.m2/repository \
    mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/target/leileme-backend-0.1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","/app/app.jar"]
