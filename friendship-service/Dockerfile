# --------------
# Dockerfile for friendship-service
# --------------

# --------------
# BUILD stage
# --------------
# base image
FROM gradle:8-jdk17 AS builder

LABEL authors="jeromedelosreyes"

# copy the application files, the host machine to the image filesystem
COPY --chown=gradle:gradle . /home/gradle/src

# set the working directory within the image, for executing future commands
WORKDIR /home/gradle/src

# run gradle build within the container - NOT the host machine
RUN gradle build -x test

# --------------
# Package Stage
# --------------
FROM openjdk:17

RUN mkdir /app

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/friendship-service.jar

ENV MONGO_DB_USERNAME=rootadmin \
    MONGO_DB_PWD=password

EXPOSE 8083

# runs the application
ENTRYPOINT ["java", "-jar", "/app/friendship-service.jar"]

# COMMANDS:
#   cd friendship-service
#   docker build -t friendship-service:1.0 .
