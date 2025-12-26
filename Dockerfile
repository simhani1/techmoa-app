FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN apk add --no-cache tzdata \
 && ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime

COPY build/libs/*-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]