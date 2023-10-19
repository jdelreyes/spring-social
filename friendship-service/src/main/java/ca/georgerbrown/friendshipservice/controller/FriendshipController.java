package ca.georgerbrown.friendshipservice.controller;

import ca.georgerbrown.friendshipservice.dto.FriendshipRequest;
import ca.georgerbrown.friendshipservice.dto.FriendshipResponse;
import ca.georgerbrown.friendshipservice.service.FriendshipServiceImpl;
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
    public ResponseEntity<Map<String, Object>> sendFriendRequest(FriendshipRequest friendshipRequest, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = friendshipService.sendFriendRequest(friendshipRequest, httpServletRequest);
        if ((Boolean) stringObjectMap.get("status"))
            return new ResponseEntity<>(stringObjectMap, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(stringObjectMap, HttpStatus.CREATED);
    }

    @PutMapping("/accept")
    public ResponseEntity<Map<String, Object>> acceptFriendRequest(FriendshipRequest friendshipRequest, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = friendshipService.acceptFriendRequest(friendshipRequest, httpServletRequest);
        if ((Boolean) stringObjectMap.get("status"))
            return new ResponseEntity<>(stringObjectMap, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(stringObjectMap, HttpStatus.OK);
    }

    @PutMapping("/reject")
    public ResponseEntity<Map<String, Object>> rejectFriendRequest(FriendshipRequest friendshipRequest, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = friendshipService.rejectFriendRequest(friendshipRequest, httpServletRequest);
        if ((Boolean) stringObjectMap.get("status"))
            return new ResponseEntity<>(stringObjectMap, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(stringObjectMap, HttpStatus.OK);
    }

    @GetMapping("/{userId}/pending/list")
    @ResponseStatus(HttpStatus.OK)
    List<FriendshipResponse> getPendingFriendList(@PathVariable String userId) {
        return friendshipService.getPendingFriendList(userId);
    }

    @GetMapping("/{userId}/accepted/list")
    @ResponseStatus(HttpStatus.OK)
    List<FriendshipResponse> getAcceptedFriendList(@PathVariable String userId) {
        return friendshipService.getAcceptedFriendList(userId);
    }

    @GetMapping("/{userId}/rejected/list")
    @ResponseStatus(HttpStatus.OK)
    List<FriendshipResponse> getRejectedFriendRequest(@PathVariable String userId) {
        return friendshipService.getRejectedFriendRequest(userId);
    }
}
