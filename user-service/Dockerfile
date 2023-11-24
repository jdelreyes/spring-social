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

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/user-service.jar

ENV SPRING_DATASOURCE_USERNAME=rootadmin
ENV SPRING_DATASOURCE_PASSWORD=password

EXPOSE 8081

# runs the application
ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]

# COMMANDS:
#   cd user-service
#   docker build -t user-service:1.0 .
