# WEB APPLICATION DEVELOPMENT USING JAVA: Assignment Documentation

## Infrastructure

![infrastructure](./assets/images/infrastructure.png)

The spring-social-microservice infrastructure consists of 4 services and these services are user, friendship, post, and
comment service. post and friendship connect to MongoDB for high scalability, while the comment and user implement
PostgresSQL. Each microservice responds to client request through exposed ports.

## Challenges

* Implement inter-service communication through docker, not locally.

## Lessons

* High scalability of microservice infrastructure
* NoSQL basics
* Docker and Postman introduction
* Inter-service communication by sending a request to a service API endpoint



## Microservice Port

User Service: `8081:8081`

Post Service: `8084:8084`

Comment Service: `8082:8082`

Friendship Service `8083:8083`

Discovery Service `8761:8761`

## MongoDB port

Post Service: `27016:27017`

Friendship Service `27014:27017`

## PostgreSQL Port

User Service: `5432:5432`

Comment Service: `5433:5432`