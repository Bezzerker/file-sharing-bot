FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /home/file-sharing-bot
COPY . .
RUN mvn clean install -DskipTests=true

FROM eclipse-temurin:17.0.8.1_1-jre-jammy
ARG SERVICE_NAME
WORKDIR /usr/local/$SERVICE_NAME
COPY --from=builder /home/file-sharing-bot/$SERVICE_NAME/target/application.jar .
ENTRYPOINT ["java", "-jar", "application.jar"]