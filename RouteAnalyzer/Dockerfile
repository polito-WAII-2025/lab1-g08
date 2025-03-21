FROM gradle:8.10.2-jdk17-alpine AS build

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts /app/

COPY src /app/src

RUN gradle build

FROM openjdk:17-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*-all.jar /app/app.jar

CMD ["java", "-jar", "app.jar"]