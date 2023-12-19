package ca.springsocial.friendshipservice;

import ca.springsocial.friendshipservice.dto.friendship.FriendshipRecipientRequest;
import ca.springsocial.friendshipservice.dto.friendship.FriendshipRequesterRequest;
import ca.springsocial.friendshipservice.dto.friendship.FriendshipResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"management.server.port=0"})
public class FriendshipServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    // FRIEND REQUEST
    @Test
    void sendFriendRequestTest() {
        // "SENDING" ENDPOINT
        String url = "http://localhost:" + port + "/api/friendships/send";

        FriendshipRecipientRequest friendshipRequesterRequest = FriendshipRecipientRequest.builder()
                .recipientUserId(123L)
                .build();

        // Send the POST request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FriendshipRecipientRequest> entity = new HttpEntity<>(friendshipRequesterRequest, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        // Assertions
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    // ACCEPT
    @Test
    void acceptFriendRequestTest() {
        // "ACCEPT" ENDPOINT
        String url = "http://localhost:" + port + "/api/friendships/accept";


        FriendshipRequesterRequest friendshipRequesterRequest = FriendshipRequesterRequest.builder()
                .requesterId(Long.valueOf("12345"))
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FriendshipRequesterRequest> entity = new HttpEntity<>(friendshipRequesterRequest, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Map.class);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void rejectFriendRequestTest() {
        // "REJECT" ENDPOINT
        String url = "http://localhost:" + port + "/api/friendships/reject";


        FriendshipRequesterRequest friendshipRequesterRequest = FriendshipRequesterRequest.builder()
                .requesterId(Long.valueOf("12345"))
                .build();

        // Send the PUT request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FriendshipRequesterRequest> entity = new HttpEntity<>(friendshipRequesterRequest, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Map.class);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    void getPendingFriendListTest() {
        Long userId = 123L;
        String url = "http://localhost:" + port + "/api/friendships/" + userId + "/pending/list";

        // Send the GET request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<List<FriendshipResponse>> response = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<FriendshipResponse>>() {
        });

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<FriendshipResponse> friendshipResponses = response.getBody();
        assertNotNull(friendshipResponses);

    }


}
