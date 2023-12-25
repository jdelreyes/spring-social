package ca.springsocial.userservice.controller;

import ca.springsocial.userservice.dto.combined.UserWithComments;
import ca.springsocial.userservice.dto.combined.UserWithPosts;
import ca.springsocial.userservice.dto.user.UserRequest;
import ca.springsocial.userservice.dto.user.UserResponse;
import ca.springsocial.userservice.service.UserServiceImpl;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getUsers() {
        return userService.getUsers();
    }

    @PostMapping()
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody UserRequest userRequest) {
        Map<String, Object> userHashMap = userService.createUser(userRequest);

        if ((Boolean) userHashMap.get("status"))
            return new ResponseEntity<>(userHashMap, HttpStatus.CREATED);

        return new ResponseEntity<>(userHashMap, HttpStatus.CONFLICT);
    }

    @PutMapping({"/{userId}"})
    public ResponseEntity<?> updateUser(@PathVariable("userId") Long userId, @RequestBody UserRequest userRequest) {
        boolean isUserUpdated = userService.updateUser(userId, userRequest);
        if (!isUserUpdated) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping({"/{userId}"})
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping({"/{userId}"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        UserResponse userResponse = userService.getUserById(userId);
        if (userResponse != null)
            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @CircuitBreaker(name = "circuitBreakerService", fallbackMethod = "getUserPostsFallback")
    @TimeLimiter(name = "circuitBreakerService")
    @Retry(name = "circuitBreakerService")
    @GetMapping({"/{userId}/posts"})
    public CompletableFuture<ResponseEntity<UserWithPosts>> getUserPosts(@PathVariable Long userId) {
        ResponseEntity<UserWithPosts> userWithPostsResponseEntity = userService.getUserWithPosts(userId);
        return CompletableFuture.supplyAsync(() -> userWithPostsResponseEntity);
    }

    @CircuitBreaker(name = "circuitBreakerService", fallbackMethod = "getUserCommentsFallback")
    @TimeLimiter(name = "circuitBreakerService")
    @Retry(name = "circuitBreakerService")
    @GetMapping({"/{userId}/comments"})
    public CompletableFuture<ResponseEntity<UserWithComments>> getUserComments(@PathVariable Long userId) {
        ResponseEntity<UserWithComments> userWithCommentsResponseEntity = userService.getUserWithComments(userId);
        return CompletableFuture.supplyAsync(() -> userWithCommentsResponseEntity);
    }

    // fallback methods
    public CompletableFuture<ResponseEntity<UserWithPosts>> getUserPostsFallback(Long userId,
                                                                                 RuntimeException runtimeException) {
        return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));
    }

    public CompletableFuture<ResponseEntity<UserWithComments>> getUserCommentsFallback(Long userId,
                                                                                       RuntimeException runtimeException) {
        return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));
    }
}
