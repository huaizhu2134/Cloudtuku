# Docker 镜像构建
FROM maven:3.8.8-eclipse-temurin-17 as builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=builder /app/target/springboot-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8101
CMD ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]