package ca.georgerbrown.friendshipservice.service;

import ca.georgerbrown.friendshipservice.dto.FriendshipResponse;
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
public class FriendshipServiceImpl implements FriendshipService{
    private final MongoTemplate mongoTemplate;
    private final FriendshipRepository friendshipRepository;

    @Override
    public Map<String, Object> sendFriendRequest(String recipientId, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = validateUserIdFromCookie(httpServletRequest);
        if (!(Boolean) stringObjectMap.get("status"))
            return stringObjectMap;

        return null;
    }

    @Override
    public Map<String, Object> acceptFriendRequest(String recipientId, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = validateUserIdFromCookie(httpServletRequest);
        if (!(Boolean) stringObjectMap.get("status"))
            return stringObjectMap;
        return null;
    }

    @Override
    public Map<String, Object> rejectFriendRequest(String recipientId, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = validateUserIdFromCookie(httpServletRequest);
        if (!(Boolean) stringObjectMap.get("status"))
            return stringObjectMap;
        return null;
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

    private Map<String, Object> validateUserIdFromCookie(HttpServletRequest httpServletRequest) {
        String userId = getUserIdFromCookie(httpServletRequest);
        if (userId == null)
            return new HashMap<String, Object>(){{put("status", false);put("message", "no logged in user");}};
        return new HashMap<String, Object>(){{put("status", true);put("userId", userId);}};
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
