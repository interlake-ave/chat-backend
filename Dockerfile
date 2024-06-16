FROM gradle:8-jdk8 AS build
WORKDIR /app
COPY --chown=gradle:gradle . ./
RUN gradle build --no-daemon

FROM openjdk:8-jre-slim

RUN mkdir /app

COPY --from=build /app/app/build /app

ENTRYPOINT ["java","-jar","/app/build/libs/app.jar"]