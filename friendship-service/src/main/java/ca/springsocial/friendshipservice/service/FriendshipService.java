package ca.springsocial.friendshipservice.service;

import ca.springsocial.friendshipservice.dto.friendship.FriendshipRecipientRequest;
import ca.springsocial.friendshipservice.dto.friendship.FriendshipRequesterRequest;
import ca.springsocial.friendshipservice.dto.friendship.FriendshipResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface FriendshipService {
    ResponseEntity<Map<String, Object>> sendFriendRequest(FriendshipRecipientRequest friendshipRecipientRequest, HttpServletRequest httpServletRequest);

    Map<String, Object> acceptFriendRequest(FriendshipRequesterRequest friendshipRequesterRequest, HttpServletRequest httpServletRequest);

    Map<String, Object> rejectFriendRequest(FriendshipRequesterRequest friendshipRequesterRequest, HttpServletRequest httpServletRequest);

    List<FriendshipResponse> getPendingFriendList(Long userId);

    List<FriendshipResponse> getAcceptedFriendList(Long userId);

    List<FriendshipResponse> getRejectedFriendList(Long userId);

    List<FriendshipResponse> getFriendships();

    FriendshipResponse getFriendship(String friendshipId);
}
