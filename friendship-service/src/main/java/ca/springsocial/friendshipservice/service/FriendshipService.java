package ca.springsocial.friendshipservice.service;

import ca.springsocial.friendshipservice.dto.friendship.FriendshipRequest;
import ca.springsocial.friendshipservice.dto.friendship.FriendshipResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface FriendshipService {
    ResponseEntity<?> sendFriendRequest(FriendshipRequest friendshipRequest);

    Map<String, Object> acceptFriendRequest(FriendshipRequest friendshipRequest);

    Map<String, Object> rejectFriendRequest(FriendshipRequest friendshipRequest);

    List<FriendshipResponse> getPendingFriendList(Long userId);

    List<FriendshipResponse> getAcceptedFriendList(Long userId);

    List<FriendshipResponse> getRejectedFriendList(Long userId);

    List<FriendshipResponse> getFriendships();

    FriendshipResponse getFriendshipById(String friendshipId);
}
