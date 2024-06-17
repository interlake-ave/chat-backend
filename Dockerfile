FROM gradle:8-jdk8 AS build
WORKDIR /app
COPY --chown=gradle:gradle . ./

RUN gradle build

#FROM amazoncorretto:22-alpine
FROM openjdk:8-jre-slim
EXPOSE 8080
RUN mkdir /app

#COPY --from=build /app/app/build/libs /app
COPY --from=build /app/app/build/distributions/app.tar /app/
WORKDIR /app
RUN tar -xvf app.tar
WORKDIR /app/app
CMD bin/app

#ENTRYPOINT ["java","-jar","/app/app.jar"]
