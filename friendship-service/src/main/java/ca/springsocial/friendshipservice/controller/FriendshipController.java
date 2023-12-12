package ca.springsocial.friendshipservice.controller;

import ca.springsocial.friendshipservice.dto.friendship.FriendshipRequest;
import ca.springsocial.friendshipservice.dto.friendship.FriendshipResponse;
import ca.springsocial.friendshipservice.service.FriendshipServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friendships")
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipServiceImpl friendshipService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendFriendRequest(@RequestBody FriendshipRequest friendshipRequest, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = friendshipService.sendFriendRequest(friendshipRequest, httpServletRequest);
        if ((Boolean) stringObjectMap.get("status"))
            return new ResponseEntity<>(stringObjectMap, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(stringObjectMap, HttpStatus.CREATED);
    }

    @PutMapping("/accept")
    public ResponseEntity<Map<String, Object>> acceptFriendRequest(@RequestBody FriendshipRequest friendshipRequest, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = friendshipService.acceptFriendRequest(friendshipRequest, httpServletRequest);
        if ((Boolean) stringObjectMap.get("status"))
            return new ResponseEntity<>(stringObjectMap, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(stringObjectMap, HttpStatus.OK);
    }

    @PutMapping("/reject")
    public ResponseEntity<Map<String, Object>> rejectFriendRequest(@RequestBody FriendshipRequest friendshipRequest, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = friendshipService.rejectFriendRequest(friendshipRequest, httpServletRequest);
        if ((Boolean) stringObjectMap.get("status"))
            return new ResponseEntity<>(stringObjectMap, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(stringObjectMap, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/pending-list")
    @ResponseStatus(HttpStatus.OK)
    List<FriendshipResponse> getPendingFriendList(@PathVariable Long userId) {
        return friendshipService.getPendingFriendList(userId);
    }

    @GetMapping("/user/{userId}/accepted-list")
    @ResponseStatus(HttpStatus.OK)
    List<FriendshipResponse> getAcceptedFriendList(@PathVariable Long userId) {
        return friendshipService.getAcceptedFriendList(userId);
    }

    @GetMapping("/user/{userId}/rejected-list")
    @ResponseStatus(HttpStatus.OK)
    List<FriendshipResponse> getRejectedFriendRequest(@PathVariable Long userId) {
        return friendshipService.getRejectedFriendList(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<FriendshipResponse> getFriendships() {
        return friendshipService.getFriendships();
    }

    @GetMapping("{friendshipId}/details")
    @ResponseStatus(HttpStatus.OK)
    FriendshipResponse getFriendship(@PathVariable String friendshipId) {
        return friendshipService.getFriendship(friendshipId);
    }

}
