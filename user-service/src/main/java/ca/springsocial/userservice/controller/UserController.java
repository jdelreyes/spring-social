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

    // CREATE
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody UserRequest userRequest) {
        Map<String, Object> userHashMap = userService.signUp(userRequest);

        if ((Boolean) userHashMap.get("status"))
            return new ResponseEntity<>(userHashMap, HttpStatus.CREATED);

        return new ResponseEntity<>(userHashMap, HttpStatus.CONFLICT);
    }

    @PostMapping({"/login"})
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserRequest userRequest, HttpServletResponse response) {
        Map<String, Object> userHashMap = userService.login(userRequest.getUserName(), userRequest.getPassword(), response);

        if ((Boolean) userHashMap.get("status"))
            return new ResponseEntity<>(userHashMap, HttpStatus.OK);

        return new ResponseEntity<>(userHashMap, HttpStatus.BAD_REQUEST);
    }

    @PostMapping({"/logout"})
    @ResponseStatus(HttpStatus.OK)
    Map<String, Object> logout(HttpServletResponse httpServletResponse) {
        return userService.logout(httpServletResponse);
    }

    @GetMapping({"/{userId}"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        UserResponse userResponse = userService.getUserById(userId);
        if (userResponse != null)
            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getUsers() {
        return userService.getUsers();
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

    @CircuitBreaker(name = "circuitBreakerService", fallbackMethod = "getUserPostsFallback")
    @TimeLimiter(name = "circuitBreakerService")
    @Retry(name = "circuitBreakerService")
    @GetMapping({"/{userId}/posts"})
    public CompletableFuture<ResponseEntity<UserWithPosts>> getUserPosts(@PathVariable Long userId) {
        UserWithPosts userWithPosts = userService.getUserWithPosts(userId);

        if (userWithPosts == null) {
            return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        }
        return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(userWithPosts, HttpStatus.OK));
    }

    public CompletableFuture<ResponseEntity<?>> getUserPostsFallback() {
        return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));
    }

    @CircuitBreaker(name = "circuitBreakerService", fallbackMethod = "getUserCommentsFallback")
    @TimeLimiter(name = "circuitBreakerService")
    @Retry(name = "circuitBreakerService")
    @GetMapping({"/{userId}/comments"})
    public CompletableFuture<ResponseEntity<UserWithComments>> getUserComments(@PathVariable Long userId) {
        UserWithComments userWithComments = userService.getUserWithComments(userId);
        if (userWithComments == null) {
            return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        }
        return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(userWithComments, HttpStatus.OK));
    }

    public CompletableFuture<ResponseEntity<?>> getUserCommentsFallback() {
        return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));
    }
}
