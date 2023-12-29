package ca.springsocial.friendshipservice.service;

import ca.springsocial.friendshipservice.dto.friendship.FriendshipRequest;
import ca.springsocial.friendshipservice.dto.friendship.FriendshipResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface FriendshipService {
    ResponseEntity<FriendshipResponse> sendFriendRequest(FriendshipRequest friendshipRequest);

    ResponseEntity<FriendshipResponse> acceptFriendRequest(FriendshipRequest friendshipRequest);

    ResponseEntity<FriendshipResponse> rejectFriendRequest(FriendshipRequest friendshipRequest);

    List<FriendshipResponse> getPendingFriendList(Long userId);

    List<FriendshipResponse> getAcceptedFriendList(Long userId);

    List<FriendshipResponse> getRejectedFriendList(Long userId);

    List<FriendshipResponse> getFriendships();

    FriendshipResponse getFriendshipById(String friendshipId);
}
