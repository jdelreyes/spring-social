package ca.georgebrown.postservice;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

public abstract class AbstractContainerBaseTest {
    static final MongoDBContainer MONGO_DB_CONTAINER;

    static {
        MONGO_DB_CONTAINER = new MongoDBContainer("mongo:latest");
        MONGO_DB_CONTAINER.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {

        // NOTE: Is this "spring.data.mongodb.uri" or "spring.data.mongo.uri" ?? :D
        // yea
        dynamicPropertyRegistry.add("spring.data.mongodb.uri",
                MONGO_DB_CONTAINER::getReplicaSetUrl);
    }
}