# WEB APPLICATION DEVELOPMENT USING JAVA: Assignment

## Docker Compose
Running this command will let you run the application through `Docker`. Make sure `Docker` is running.

```shell
docker-compose -p spring-social -f docker-compose.yml up -d
```

## Docker Run
<details>
<summary>Running these commands will let you run the application through your IDE.</summary>

```shell
# network
docker network create spring-social
# database
docker run -d --name user-service --network=spring-social -p 5432:5432 -e POSTGRES_USER=rootadmin -e POSTGRES_PASSWORD=password -e POSTGRES_DB=user-service --restart unless-stopped postgres:latest
docker run -d --name post-service --network=spring-social -p 27016:27017 -e MONGO_INITDB_ROOT_USERNAME=rootadmin -e MONGO_INITDB_ROOT_PASSWORD=password --restart unless-stopped mongo:latest
docker run -d --name comment-service --network=spring-social -p 5433:5432 -e POSTGRES_USER=rootadmin -e POSTGRES_PASSWORD=password -e POSTGRES_DB=comment-service --restart unless-stopped postgres:latest
docker run -d --name friendship-service --network=spring-social -p 27014:27017 -e MONGO_INITDB_ROOT_USERNAME=rootadmin -e MONGO_INITDB_ROOT_PASSWORD=password --restart unless-stopped mongo:latest 
```

</details>

## Microservice Port
User Service: `8080:8080`

Post Service: `8084:8084`

Comment Service: `8082:8082`

Friendship Service `8083:8083`

Discovery Service `8761:8761`

## MongoDB port
Post Service: `27016:27017`

Friendship Service `27014:27017`

## PostgresSQL Port
User Service: `5432:5432`

Comment Service: `5433:5432`