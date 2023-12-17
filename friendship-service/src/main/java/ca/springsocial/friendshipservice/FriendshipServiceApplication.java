package ca.springsocial.friendshipservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FriendshipServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FriendshipServiceApplication.class, args);
    }

}
