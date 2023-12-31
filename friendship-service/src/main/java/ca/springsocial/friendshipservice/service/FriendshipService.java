package ca.springsocial.friendshipservice.service;

import ca.springsocial.friendshipservice.dto.friendship.FriendshipRequest;
import ca.springsocial.friendshipservice.dto.friendship.FriendshipResponse;
import ca.springsocial.friendshipservice.enums.FriendshipStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface FriendshipService {
    ResponseEntity<FriendshipResponse> sendFriendRequest(FriendshipRequest friendshipRequest);

    ResponseEntity<FriendshipResponse> acceptFriendRequest(FriendshipRequest friendshipRequest);

    ResponseEntity<FriendshipResponse> rejectFriendRequest(FriendshipRequest friendshipRequest);

    List<FriendshipResponse> getFriendListByFriendshipStatus(FriendshipStatus friendshipStatus);

    List<FriendshipResponse> getUserFriendListByFriendshipStatus(Long userId, FriendshipStatus friendshipStatus);

    List<FriendshipResponse> getFriendships();

    FriendshipResponse getFriendshipById(String friendshipId);
}
