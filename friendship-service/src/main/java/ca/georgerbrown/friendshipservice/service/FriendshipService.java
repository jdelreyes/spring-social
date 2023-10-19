package ca.georgerbrown.friendshipservice.service;

import ca.georgerbrown.friendshipservice.dto.FriendshipRequest;
import ca.georgerbrown.friendshipservice.dto.FriendshipResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface FriendshipService {
    Map<String, Object> sendFriendRequest(FriendshipRequest friendshipRequest,HttpServletRequest httpServletRequest);
    Map<String, Object> acceptFriendRequest(FriendshipRequest friendshipRequest,HttpServletRequest httpServletRequest);
    Map<String, Object> rejectFriendRequest(FriendshipRequest friendshipRequest,HttpServletRequest httpServletRequest);
    List<FriendshipResponse> getPendingFriendList(String userId);
    List<FriendshipResponse> getAcceptedFriendList(String userId);
    List<FriendshipResponse> getRejectedFriendRequest(String userId);
}
