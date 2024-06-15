
FROM openjdk:8-alpine as builder

# johnrengelman/shadow v5.2.0 requires Gradle v5
#   but openjdk:8-alpine packaged with Alpine v3.9
#   which packaged with Gradle v4.10.3
RUN set -x \
  && apk add --update --no-cache --repository=http://nl.alpinelinux.org/alpine/v3.10/community gradle

# Install deps before copying code, use docker cache for external packages
WORKDIR app
COPY build.gradle /app/
RUN set -x && gradle compileJava

# Copy source tree and build everything
COPY src /app/src
RUN set -x && gradle build

# Prepare small runtime image
FROM openjdk:8-alpine
COPY --from=builder /app/build/libs/app-all.jar /app/
ENTRYPOINT ["java", "-jar", "app/app-all.jar"]
