FROM gradle:8-jdk8 AS build
WORKDIR /app
COPY --chown=gradle:gradle . ./

RUN gradle build

#FROM amazoncorretto:22-alpine
FROM openjdk:8-jre-slim

RUN mkdir /app

COPY --from=build /app/app/build/libs /app

ENTRYPOINT ["java","-jar","/app/app.jar"]
