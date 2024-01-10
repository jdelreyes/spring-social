# --------------
# BUILD stage
# --------------
# base image
FROM gradle:8-jdk17-alpine AS builder

LABEL authors="Jerome Delos Reyes"

# copy the application files, the host machine to the image filesystem
COPY --chown=gradle:gradle . /home/gradle/src

# set the working directory within the image, for executing future commands
WORKDIR /home/gradle/src

# run gradle build within the container - NOT the host machine
RUN gradle build -x test

# --------------
# Package Stage
# --------------
FROM openjdk:17-alpine

RUN mkdir /app

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/feed-service.jar

EXPOSE 8086

# runs the application
ENTRYPOINT ["java", "-jar", "/app/feed-service.jar"]

# COMMANDS:
#   cd feed-service
#   docker build -t feed-service:1.0 .
