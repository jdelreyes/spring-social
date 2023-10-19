package ca.georgerbrown.friendshipservice.service;

import ca.georgerbrown.friendshipservice.dto.FriendshipResponse;
import ca.georgerbrown.friendshipservice.enums.FriendshipStatus;
import ca.georgerbrown.friendshipservice.model.Friendship;
import ca.georgerbrown.friendshipservice.repository.FriendshipRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendshipServiceImpl implements FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public Map<String, Object> sendFriendRequest(String recipientId, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = validateUserIdFromCookie(httpServletRequest);
        if (!(Boolean) stringObjectMap.get("status"))
            return stringObjectMap;

        Friendship friendship = friendshipRepository.findByRecipientUserIdAndRequesterUserId(recipientId,
                (String) stringObjectMap.get("userId"));

        if (isFriendRequestAccepted(recipientId, (String) stringObjectMap.get("userId")) ||
                isFriendRequestPending(recipientId, (String) stringObjectMap.get("userId"))) {
            return new HashMap<String, Object>() {{
                put("status", false);
                put("message", "friend request already pending or accepted");
            }};
        }

        // it exists, just need to change status to pending
        if (isFriendRequestRejected(recipientId, (String) stringObjectMap.get("userId"))) {
            friendship.setStatus(FriendshipStatus.PENDING);
        }

        // does not exist in document, making one
        if (friendship == null) {
            friendship = Friendship.builder()
                    .requesterUserId((String) stringObjectMap.get("userId"))
                    .recipientUserId(recipientId)
                    .status(FriendshipStatus.PENDING)
                    .build();
        }

        friendshipRepository.save(friendship);

        return new HashMap<String, Object>() {{
            put("status", true);
            put("message", "successfully sent request");
        }};
    }

    @Override
    public Map<String, Object> acceptFriendRequest(String recipientId, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = validateUserIdFromCookie(httpServletRequest);
        if (!(Boolean) stringObjectMap.get("status"))
            return stringObjectMap;

        if (isFriendRequestPending(recipientId, (String) stringObjectMap.get("userId")))
            return new HashMap<String, Object>() {{
                put("status", false);
                put("message", "no pending request");
            }};

        Friendship friendship = friendshipRepository.findByRecipientUserIdAndRequesterUserId(recipientId,
                (String) stringObjectMap.get("userid"));

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);

        return new HashMap<String, Object>() {{
            put("status", true);
            put("message", "successfully sent request");
        }};
    }

    @Override
    public Map<String, Object> rejectFriendRequest(String recipientId, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = validateUserIdFromCookie(httpServletRequest);
        if (!(Boolean) stringObjectMap.get("status"))
            return stringObjectMap;

        if (isFriendRequestPending(recipientId, (String) stringObjectMap.get("userId")))
            return new HashMap<String, Object>() {{
                put("status", false);
                put("message", "no pending request");
            }};

        Friendship friendship = friendshipRepository.findByRecipientUserIdAndRequesterUserId(recipientId,
                (String) stringObjectMap.get("userid"));

        friendship.setStatus(FriendshipStatus.REJECTED);
        friendshipRepository.save(friendship);

        return new HashMap<String, Object>() {{
            put("status", true);
            put("message", "successfully rejected");
        }};
    }

    @Override
    public List<FriendshipResponse> getPendingFriendList(String userId) {
        return null;
    }

    @Override
    public List<FriendshipResponse> getAcceptedFriendList(String userId) {
        return null;
    }

    @Override
    public List<FriendshipResponse> getRejectedFriendRequest(String userId) {
        return null;
    }

    private boolean isFriendRequestPending(String recipientUserId, String requesterId) {
        Friendship friendship = friendshipRepository.findByRecipientUserIdAndRequesterUserId(recipientUserId, requesterId);
        return friendship.getStatus() != FriendshipStatus.PENDING;
    }

    private boolean isFriendRequestAccepted(String recipientUserId, String requesterId) {
        Friendship friendship = friendshipRepository.findByRecipientUserIdAndRequesterUserId(recipientUserId, requesterId);
        return friendship.getStatus() == FriendshipStatus.ACCEPTED;
    }

    private boolean isFriendRequestRejected(String recipientUserId, String requesterId) {
        Friendship friendship = friendshipRepository.findByRecipientUserIdAndRequesterUserId(recipientUserId, requesterId);
        return friendship.getStatus() == FriendshipStatus.REJECTED;
    }

    private Map<String, Object> validateUserIdFromCookie(HttpServletRequest httpServletRequest) {
        String userId = getUserIdFromCookie(httpServletRequest);
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

    private String getUserIdFromCookie(HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("remember-me".equals(cookie.getName())) {
                    // userId value;
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private Friendship queryFriendship(String key, Object value) {
        Query query = new Query();
        query.addCriteria(Criteria.where(key).is(value));
        return mongoTemplate.findOne(query, Friendship.class);
    }

    private List<Friendship> queryFriendships(String key, Object value) {
        Query query = new Query();
        query.addCriteria(Criteria.where(key).is(value));
        return mongoTemplate.find(query, Friendship.class);
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
