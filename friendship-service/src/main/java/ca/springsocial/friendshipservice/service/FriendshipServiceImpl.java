package ca.springsocial.friendshipservice.service;

import ca.springsocial.friendshipservice.dto.friendship.FriendshipRequest;
import ca.springsocial.friendshipservice.dto.friendship.FriendshipResponse;
import ca.springsocial.friendshipservice.dto.user.UserResponse;
import ca.springsocial.friendshipservice.enums.FriendshipStatus;
import ca.springsocial.friendshipservice.model.Friendship;
import ca.springsocial.friendshipservice.repository.FriendshipRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final FriendshipRepository friendshipRepository;
    private final MongoTemplate mongoTemplate;
    private final WebClient webClient;

    @Value("${user.service.url}")
    public String userServiceUri;

    @Override
    public ResponseEntity<Map<String, Object>> sendFriendRequest(FriendshipRequest friendshipRequest, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = validateUserIdFromCookie(httpServletRequest);
        Long requesterUserId = (Long) stringObjectMap.get("userId");

        if (!(Boolean) stringObjectMap.get("status"))
            return new ResponseEntity<>(stringObjectMap, HttpStatus.FORBIDDEN);

        Long recipientUserId = friendshipRequest.getRecipientUserId();

        Friendship friendship = friendshipRepository.findByRequesterUserIdAndRecipientUserId(recipientUserId,
                requesterUserId);

        if (friendship == null) {
            // does not exist in document, making one
            friendship = Friendship.builder()
                    .requesterUserId(requesterUserId)
                    .recipientUserId(recipientUserId)
                    .status(FriendshipStatus.pending)
                    .build();
        }

        String id = friendshipRepository.save(friendship).getId();

        return new ResponseEntity<>(new HashMap<String, Object>() {{
            put("status", true);
            put("id", id);
        }}, HttpStatus.CREATED);
    }

    @Override
    public Map<String, Object> acceptFriendRequest(FriendshipRequest friendshipRequest, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = validateUserIdFromCookie(httpServletRequest);
        if (!(Boolean) stringObjectMap.get("status"))
            return stringObjectMap;

        Long recipientUserId = friendshipRequest.getRecipientUserId();

        if (isFriendRequestPending(recipientUserId, (Long) stringObjectMap.get("userId")))
            return new HashMap<String, Object>() {{
                put("status", false);
                put("message", "no pending request");
            }};

        Friendship friendship = friendshipRepository.findByRequesterUserIdAndRecipientUserId(recipientUserId,
                (Long) stringObjectMap.get("userId"));

        friendship.setStatus(FriendshipStatus.accepted);
        friendshipRepository.save(friendship);

        return new HashMap<String, Object>() {{
            put("status", true);
            put("message", "successfully sent request");
        }};
    }

    @Override
    public Map<String, Object> rejectFriendRequest(FriendshipRequest friendshipRequest, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = validateUserIdFromCookie(httpServletRequest);
        if (!(Boolean) stringObjectMap.get("status"))
            return stringObjectMap;

        Long recipientUserId = friendshipRequest.getRecipientUserId();

        if (isFriendRequestPending(recipientUserId, (Long) stringObjectMap.get("userId")))
            return new HashMap<String, Object>() {{
                put("status", false);
                put("message", "no pending request");
            }};

        Friendship friendship = friendshipRepository.findByRequesterUserIdAndRecipientUserId(recipientUserId,
                (Long) stringObjectMap.get("userId"));

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
    public FriendshipResponse getFriendship(String friendshipId) {
        return mapToFriendshipResponse(friendshipRepository.findById(friendshipId));
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
    private boolean isFriendRequestPending(Long recipientUserId, Long requesterId) {
        Friendship friendship = friendshipRepository.findByRequesterUserIdAndRecipientUserId(requesterId,
                recipientUserId);
        return friendship.getStatus() != FriendshipStatus.pending;
    }

    private boolean isFriendRequestAccepted(Long recipientUserId, Long requesterId) {
        Friendship friendship = friendshipRepository.findByRequesterUserIdAndRecipientUserId(requesterId,
                recipientUserId);
        return friendship.getStatus() == FriendshipStatus.accepted;
    }

    private boolean isFriendRequestRejected(Long recipientUserId, Long requesterId) {
        Friendship friendship = friendshipRepository.findByRequesterUserIdAndRecipientUserId(requesterId,
                recipientUserId);
        return friendship.getStatus() == FriendshipStatus.rejected;
    }

    private Map<String, Object> validateUserIdFromCookie(HttpServletRequest httpServletRequest) {
        Long userId = getUserIdFromCookie(httpServletRequest);
        if (userId == null)
            return new HashMap<String, Object>() {{
                put("status", false);
                put("message", "no logged in user");
            }};
        return new HashMap<String, Object>() {{
            put("status", true);
            put("userId", userId);
        }};
    }

    private Long getUserIdFromCookie(HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("remember-me".equals(cookie.getName())) {
                    // userId value;
                    return Long.parseLong(cookie.getValue());
                }
            }
        }

        return null;
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
