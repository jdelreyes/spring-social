package ca.georgerbrown.friendshipservice.controller;

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
@RequestMapping("/api/friendship")
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipServiceImpl friendshipService;

    @PostMapping("/send/{recipientId}")
    public ResponseEntity<Map<String, Object>> sendFriendRequest(@PathVariable String recipientId, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = friendshipService.sendFriendRequest(recipientId, httpServletRequest);
        if ((Boolean) stringObjectMap.get("status"))
            return new ResponseEntity<>(stringObjectMap, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(stringObjectMap, HttpStatus.CREATED);
    }

    @PutMapping("/accept/{recipientId}")
    public ResponseEntity<Map<String, Object>> acceptFriendRequest(@PathVariable String recipientId, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = friendshipService.acceptFriendRequest(recipientId, httpServletRequest);
        if ((Boolean) stringObjectMap.get("status"))
            return new ResponseEntity<>(stringObjectMap, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(stringObjectMap, HttpStatus.OK);
    }

    @PutMapping("/reject/{recipientId}")
    public ResponseEntity<Map<String, Object>> rejectFriendRequest(@PathVariable String recipientId, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = friendshipService.rejectFriendRequest(recipientId, httpServletRequest);
        if ((Boolean) stringObjectMap.get("status"))
            return new ResponseEntity<>(stringObjectMap, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(stringObjectMap, HttpStatus.OK);
    }

    @GetMapping("/{userId}/pending")
    @ResponseStatus(HttpStatus.OK)
    List<FriendshipResponse> getPendingFriendList(@PathVariable String userId) {
        return friendshipService.getPendingFriendList(userId);
    }

    @GetMapping("/{userId}/accepted")
    @ResponseStatus(HttpStatus.OK)
    List<FriendshipResponse> getAcceptedFriendList(@PathVariable String userId) {
        return friendshipService.getAcceptedFriendList(userId);
    }

    @GetMapping("/{userId}/accepted")
    @ResponseStatus(HttpStatus.OK)
    List<FriendshipResponse> getRejectedFriendRequest(@PathVariable String userId) {
        return friendshipService.getRejectedFriendRequest(userId);
    }
}
