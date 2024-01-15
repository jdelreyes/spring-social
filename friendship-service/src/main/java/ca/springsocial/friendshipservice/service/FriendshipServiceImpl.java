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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendshipServiceImpl implements FriendshipService {
    private final KafkaTemplate<String, FriendRequestSentEvent> kafkaTemplate;
    private final FriendshipRepository friendshipRepository;
    private final MongoTemplate mongoTemplate;
    private final WebClient webClient;

    @Value("${user.service.url}")
    private String userServiceUri;

    @Override
    public ResponseEntity<FriendshipResponse> sendFriendRequest(FriendshipRequest friendshipRequest) {
        if (!userExists(friendshipRequest.getRecipientUserId()) && !userExists(friendshipRequest.getRequesterUserId()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Friendship friendship = Friendship.builder()
                .requesterUserId(friendshipRequest.getRequesterUserId())
                .recipientUserId(friendshipRequest.getRecipientUserId())
                .friendshipStatus(FriendshipStatus.pending)
                .build();

        friendshipRepository.save(friendship);
        kafkaTemplate.send("friendRequestSentEventTopic", new FriendRequestSentEvent(friendship.getId(),
                friendshipRequest.getRecipientUserId(), friendshipRequest.getRequesterUserId(),
                friendship.getFriendshipStatus()));

        return new ResponseEntity<>(mapToFriendshipResponse(friendship), HttpStatus.CREATED);

    }

    @Override
    public ResponseEntity<FriendshipResponse> acceptFriendRequest(FriendshipRequest friendshipRequest) {
        if (!isFriendRequestPending(friendshipRequest.getRecipientUserId(), friendshipRequest.getRequesterUserId()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Friendship friendship =
                friendshipRepository.findFriendshipByRecipientUserIdAndRequesterUserId(
                        friendshipRequest.getRecipientUserId(), friendshipRequest.getRequesterUserId());

// updates to accepted
        friendship.setFriendshipStatus(FriendshipStatus.accepted);
        friendship.setDateTimeStatusChanged(LocalDateTime.now());

        friendshipRepository.save(friendship);
        kafkaTemplate.send("friendRequestSentEventTopic", new FriendRequestSentEvent(friendship.getId(),
                friendshipRequest.getRecipientUserId(), friendshipRequest.getRequesterUserId(),
                friendship.getFriendshipStatus()));

        return new ResponseEntity<>(mapToFriendshipResponse(friendship), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<FriendshipResponse> rejectFriendRequest(FriendshipRequest friendshipRequest) {
        if (!isFriendRequestPending(friendshipRequest.getRecipientUserId(), friendshipRequest.getRequesterUserId()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Friendship friendship =
                friendshipRepository.findFriendshipByRecipientUserIdAndRequesterUserId(
                        friendshipRequest.getRecipientUserId(), friendshipRequest.getRequesterUserId());

        friendship.setFriendshipStatus(FriendshipStatus.rejected);
        friendship.setDateTimeStatusChanged(LocalDateTime.now());

        friendshipRepository.save(friendship);


        return new ResponseEntity<>(mapToFriendshipResponse(friendship), HttpStatus.OK);
    }

    @Override
    public List<FriendshipResponse> getFriendListByFriendshipStatus(FriendshipStatus friendshipStatus) {
        List<Friendship> friendshipList = friendshipRepository.findFriendshipByFriendshipStatus(friendshipStatus);
        return friendshipList.stream().map(this::mapToFriendshipResponse).toList();
    }

    @Override
    public List<FriendshipResponse> getUserFriendListByFriendshipStatus(Long userId, FriendshipStatus friendshipStatus) {
        List<Friendship> friendshipList =
                friendshipRepository
                        .findFriendshipByRecipientUserIdOrRequesterUserIdAndFriendshipStatus(userId, userId, friendshipStatus);
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
        Friendship friendship = friendshipRepository.findFriendshipByRecipientUserIdAndRequesterUserId(recipientUserId,
                requesterUserId);
        return friendship.getFriendshipStatus().equals(FriendshipStatus.pending);
    }

    private boolean isFriendRequestAccepted(Long recipientUserId, Long requesterUserId) {
        Friendship friendship = friendshipRepository.findFriendshipByRecipientUserIdAndRequesterUserId(recipientUserId,
                requesterUserId);
        return friendship.getFriendshipStatus().equals(FriendshipStatus.accepted);
    }

    private boolean isFriendRequestRejected(Long recipientUserId, Long requesterUserId) {
        Friendship friendship = friendshipRepository.findFriendshipByRecipientUserIdAndRequesterUserId(recipientUserId,
                requesterUserId);
        return friendship.getFriendshipStatus().equals(FriendshipStatus.rejected);
    }

    private FriendshipResponse mapToFriendshipResponse(Friendship friendship) {
        return FriendshipResponse.builder()
                .id(friendship.getId())
                .recipientUserId(friendship.getRecipientUserId())
                .requesterUserId(friendship.getRequesterUserId())
                .dateTimeStatusChanged(friendship.getDateTimeStatusChanged())
                .friendshipStatus(friendship.getFriendshipStatus())
                .build();
    }
}
