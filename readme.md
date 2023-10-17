# WEB APPLICATION USING JAVA: Assignment

## Docker Network Configuration

```shell
docker network create spring-social
docker run -d --name user-service --network=spring-social -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=rootadmin -e MONGO_INITDB_ROOT_PASSWORD=password --restart unless-stopped mongo:latest
docker run -d --name post-service --network=spring-social -p 27016:27017 -e MONGO_INITDB_ROOT_USERNAME=rootadmin -e MONGO_INITDB_ROOT_PASSWORD=password --restart unless-stopped mongo:latest
docker run -d --name comment-service --network=spring-social -p 27015:27017 -e MONGO_INITDB_ROOT_USERNAME=rootadmin -e MONGO_INITDB_ROOT_PASSWORD=password --restart unless-stopped mongo:latest 
```

## Microservice Port/URI
User Service: ```http://localhost:8080```

Post Service: ```http://localhost:8081```

Comment Service: ```http://localhost:8082```
