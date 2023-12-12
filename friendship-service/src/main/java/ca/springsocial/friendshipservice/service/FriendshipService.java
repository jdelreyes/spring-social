package ca.springsocial.friendshipservice.service;

import ca.springsocial.friendshipservice.dto.friendship.FriendshipRequest;
import ca.springsocial.friendshipservice.dto.friendship.FriendshipResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface FriendshipService {
    ResponseEntity<Map<String, Object>> sendFriendRequest(FriendshipRequest friendshipRequest, HttpServletRequest httpServletRequest);

    Map<String, Object> acceptFriendRequest(FriendshipRequest friendshipRequest, HttpServletRequest httpServletRequest);

    Map<String, Object> rejectFriendRequest(FriendshipRequest friendshipRequest, HttpServletRequest httpServletRequest);

    List<FriendshipResponse> getPendingFriendList(Long userId);

    List<FriendshipResponse> getAcceptedFriendList(Long userId);

    List<FriendshipResponse> getRejectedFriendList(Long userId);

    List<FriendshipResponse> getFriendships();

    FriendshipResponse getFriendship(String friendshipId);
}
