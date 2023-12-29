package ca.springsocial.friendshipservice.controller;

import ca.springsocial.friendshipservice.dto.friendship.FriendshipRequest;
import ca.springsocial.friendshipservice.dto.friendship.FriendshipResponse;
import ca.springsocial.friendshipservice.service.FriendshipServiceImpl;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public CompletableFuture<ResponseEntity<FriendshipResponse>> sendFriendRequest(@RequestBody
                                                                                   FriendshipRequest friendshipRequest) {
        ResponseEntity<FriendshipResponse> stringObjectMap = friendshipService.sendFriendRequest(friendshipRequest);
        return CompletableFuture.supplyAsync(() -> stringObjectMap);
    }

    @PutMapping("/accept")
    public ResponseEntity<FriendshipResponse> acceptFriendRequest(@RequestBody
                                                                  FriendshipRequest friendshipRequest) {
        return friendshipService.acceptFriendRequest(friendshipRequest);
    }

    @PutMapping("/reject")
    public ResponseEntity<FriendshipResponse> rejectFriendRequest(@RequestBody
                                                                  FriendshipRequest friendshipRequest) {
        return friendshipService.rejectFriendRequest(friendshipRequest);
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

    @GetMapping("{friendshipId}")
    @ResponseStatus(HttpStatus.OK)
    FriendshipResponse getFriendship(@PathVariable String friendshipId) {
        return friendshipService.getFriendshipById(friendshipId);
    }

    //    fallback
    public CompletableFuture<ResponseEntity<?>> sendFriendRequestFallback(FriendshipRequest friendshipRequest,
                                                                          RuntimeException runtimeException) {
        return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));
    }
}
