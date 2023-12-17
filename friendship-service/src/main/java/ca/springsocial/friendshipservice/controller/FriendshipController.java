package ca.springsocial.friendshipservice.controller;

import ca.springsocial.friendshipservice.dto.friendship.FriendshipRecipientRequest;
import ca.springsocial.friendshipservice.dto.friendship.FriendshipRequesterRequest;
import ca.springsocial.friendshipservice.dto.friendship.FriendshipResponse;
import ca.springsocial.friendshipservice.service.FriendshipServiceImpl;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/friendships")
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipServiceImpl friendshipService;

    @CircuitBreaker(name = "circuitBreakerService", fallbackMethod = "sendFriendRequestFallback")
    @TimeLimiter(name = "circuitBreakerService")
    @Retry(name = "circuitBreakerService")
    @PostMapping("/send")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> sendFriendRequest(@RequestBody FriendshipRecipientRequest friendshipRecipientRequest, HttpServletRequest httpServletRequest) {
        ResponseEntity<Map<String, Object>> stringObjectMap = friendshipService.sendFriendRequest(friendshipRecipientRequest, httpServletRequest);
        return CompletableFuture.supplyAsync(() -> stringObjectMap);
    }

    @PutMapping("/accept")
    public ResponseEntity<Map<String, Object>> acceptFriendRequest(@RequestBody FriendshipRequesterRequest friendshipRequesterRequest, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = friendshipService.acceptFriendRequest(friendshipRequesterRequest, httpServletRequest);
        if ((Boolean) stringObjectMap.get("status"))
            return new ResponseEntity<>(stringObjectMap, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(stringObjectMap, HttpStatus.OK);
    }

    @PutMapping("/reject")
    public ResponseEntity<Map<String, Object>> rejectFriendRequest(@RequestBody FriendshipRequesterRequest friendshipRequesterRequest, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = friendshipService.rejectFriendRequest(friendshipRequesterRequest, httpServletRequest);
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

    //    fallback
    public CompletableFuture<ResponseEntity<Map<String, Object>>> sendFriendRequestFallback(FriendshipRecipientRequest friendshipRecipientRequest,
                                                                                            HttpServletRequest httpServletRequest,
                                                                                            RuntimeException runtimeException) {
        return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));
    }
}
