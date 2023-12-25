package ca.springsocial.friendshipservice.service;

import ca.springsocial.friendshipservice.dto.friendship.FriendshipRequest;
import ca.springsocial.friendshipservice.dto.friendship.FriendshipResponse;
import ca.springsocial.friendshipservice.dto.user.UserResponse;
import ca.springsocial.friendshipservice.enums.FriendshipStatus;
import ca.springsocial.friendshipservice.events.FriendRequestSentEvent;
import ca.springsocial.friendshipservice.model.Friendship;
import ca.springsocial.friendshipservice.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendshipServiceImpl implements FriendshipService {
    private final KafkaTemplate<String, FriendRequestSentEvent> kafkaTemplate;
    private final FriendshipRepository friendshipRepository;
    private final MongoTemplate mongoTemplate;
    private final WebClient webClient;
    @Value("${user.service.url}")
    public String userServiceUri;

    @Override
    public ResponseEntity<?> sendFriendRequest(FriendshipRequest friendshipRequest) {
        if (userExists(friendshipRequest.getRecipientUserId()) && userExists(friendshipRequest.getRequesterUserId())) {
            Friendship friendship = Friendship.builder()
                    .requesterUserId(friendshipRequest.getRequesterUserId())
                    .recipientUserId(friendshipRequest.getRecipientUserId())
                    .status(FriendshipStatus.pending)
                    .build();

            friendshipRepository.save(friendship);
            kafkaTemplate.send("notificationTopic", new FriendRequestSentEvent(friendshipRequest.getRequesterUserId()));

            return new ResponseEntity<>(mapToFriendshipResponse(friendship), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override
    public Map<String, Object> acceptFriendRequest(FriendshipRequest friendshipRequest) {
        if (!isFriendRequestPending(friendshipRequest.getRecipientUserId(), friendshipRequest.getRequesterUserId()))
            return new HashMap<String, Object>() {{
                put("status", false);
                put("message", "no pending request");
            }};

        Friendship friendship =
                friendshipRepository.findFriendshipByRequesterUserIdAndRecipientUserId(
                        friendshipRequest.getRequesterUserId(), friendshipRequest.getRecipientUserId());

// updates to accepted
        friendship.setStatus(FriendshipStatus.accepted);
        friendshipRepository.save(friendship);

        return new HashMap<String, Object>() {{
            put("status", true);
            put("message", "successfully accepted");
        }};
    }

    @Override
    public Map<String, Object> rejectFriendRequest(FriendshipRequest friendshipRequest) {
        if (!isFriendRequestPending(friendshipRequest.getRecipientUserId(), friendshipRequest.getRequesterUserId()))
            return new HashMap<String, Object>() {{
                put("status", false);
                put("message", "no pending request");
            }};

        Friendship friendship =
                friendshipRepository.findFriendshipByRequesterUserIdAndRecipientUserId(
                        friendshipRequest.getRequesterUserId(), friendshipRequest.getRecipientUserId());

        friendship.setStatus(FriendshipStatus.rejected);
        friendshipRepository.save(friendship);

        return new HashMap<String, Object>() {{
            put("status", true);
            put("message", "successfully rejected");
        }};
    }

    @Override
    public List<FriendshipResponse> getPendingFriendList(Long userId) {
        List<Friendship> friendshipList = friendshipRepository.findAllByRecipientUserIdOrRequesterUserIdAndStatus(userId,
                userId, FriendshipStatus.pending);
        return friendshipList.stream().map(this::mapToFriendshipResponse).toList();
    }

    @Override
    public List<FriendshipResponse> getAcceptedFriendList(Long userId) {
        List<Friendship> friendshipList = friendshipRepository.findAllByRecipientUserIdOrRequesterUserIdAndStatus(userId,
                userId, FriendshipStatus.accepted);
        return friendshipList.stream().map(this::mapToFriendshipResponse).toList();
    }

    @Override
    public List<FriendshipResponse> getRejectedFriendList(Long userId) {
        List<Friendship> friendshipList = friendshipRepository.findAllByRecipientUserIdOrRequesterUserIdAndStatus(userId,
                userId, FriendshipStatus.rejected);
        return friendshipList.stream().map(this::mapToFriendshipResponse).toList();
    }

    @Override
    public List<FriendshipResponse> getFriendships() {
        return friendshipRepository.findAll().stream().map(this::mapToFriendshipResponse).toList();
    }

    @Override
    public FriendshipResponse getFriendshipById(String friendshipId) {
        return mapToFriendshipResponse(friendshipRepository.findFriendshipById(friendshipId));
    }

    private Boolean userExists(Long userId) {
        UserResponse userResponse = webClient.get()
                .uri(userServiceUri + "/" + userId)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, ex -> ex.getStatusCode().is4xxClientError()
                        ? Mono.empty()
                        : Mono.error(ex))
                .block();

        return userResponse != null;
    }

    //  helper functions
    private boolean isFriendRequestPending(Long recipientUserId, Long requesterUserId) {
        Friendship friendship = friendshipRepository.findFriendshipByRequesterUserIdAndRecipientUserId(requesterUserId,
                recipientUserId);
        return friendship.getStatus() == FriendshipStatus.pending;
    }

    private boolean isFriendRequestAccepted(Long recipientUserId, Long requesterUserId) {
        Friendship friendship = friendshipRepository.findFriendshipByRequesterUserIdAndRecipientUserId(requesterUserId,
                recipientUserId);
        return friendship.getStatus() == FriendshipStatus.accepted;
    }

    private boolean isFriendRequestRejected(Long recipientUserId, Long requesterUserId) {
        Friendship friendship = friendshipRepository.findFriendshipByRequesterUserIdAndRecipientUserId(requesterUserId,
                recipientUserId);
        return friendship.getStatus() == FriendshipStatus.rejected;
    }

    private FriendshipResponse mapToFriendshipResponse(Friendship friendship) {
        return FriendshipResponse.builder()
                .id(friendship.getId())
                .recipientUserId(friendship.getRecipientUserId())
                .requesterUserId(friendship.getRequesterUserId())
                .status(friendship.getStatus())
                .build();
    }
}
