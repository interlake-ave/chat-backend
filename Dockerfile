FROM gradle:8-jdk8 AS build
WORKDIR /app
COPY --chown=gradle:gradle . ./

RUN gradle build

FROM openjdk:8-jre-slim

RUN mkdir /app

COPY --from=build /app/app/build/distributions/app.tar /app/
WORKDIR /app
RUN tar -xvf app.tar
WORKDIR /app/app
CMD bin/app