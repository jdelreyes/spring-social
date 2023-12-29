package ca.springsocial.postservice.controller;

import ca.springsocial.postservice.dto.combined.PostWithComments;
import ca.springsocial.postservice.dto.post.PostRequest;
import ca.springsocial.postservice.dto.post.PostResponse;
import ca.springsocial.postservice.service.PostServiceImpl;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostServiceImpl postService;

    @CircuitBreaker(name = "circuitBreakerService", fallbackMethod = "createPostFallback")
    @TimeLimiter(name = "circuitBreakerService")
    @Retry(name = "circuitBreakerService")
    @PostMapping
    public CompletableFuture<ResponseEntity<PostResponse>> createPost(@RequestBody PostRequest postRequest) {
        ResponseEntity<PostResponse> postResponseResponseEntity = postService.createPost(postRequest);
        return CompletableFuture.supplyAsync(() -> postResponseResponseEntity);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable("postId") String postId, @RequestBody PostRequest postRequest) {
        return postService.updatePost(postId, postRequest);
    }

    @DeleteMapping({"/{postId}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable("postId") String postId) {
        postService.deletePost(postId);
    }

    @GetMapping({"/{postId}"})
    public ResponseEntity<PostResponse> getPostById(@PathVariable("postId") String postId) {
        PostResponse postResponse = postService.getPostById(postId);
        if (postResponse != null) {
            return new ResponseEntity<>(postResponse, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponse> getPosts(@RequestParam("userId") Optional<Long> userId) {
        if (userId.isPresent())
            return postService.getUserPosts(userId.get());
        return postService.getPosts();
    }

    @CircuitBreaker(name = "circuitBreakerService", fallbackMethod = "getPostCommentsFallback")
    @TimeLimiter(name = "circuitBreakerService")
    @Retry(name = "circuitBreakerService")
    @GetMapping({"/{postId}/comments"})
    public CompletableFuture<ResponseEntity<PostWithComments>> getPostComments(@PathVariable String postId) {
        ResponseEntity<PostWithComments> postWithCommentsResponseEntity = postService.getPostWithComments(postId);
        return CompletableFuture.supplyAsync(() -> postWithCommentsResponseEntity);
    }

    //    fallback methods
    public CompletableFuture<ResponseEntity<PostWithComments>> getPostCommentsFallback(String postId,
                                                                                       RuntimeException runtimeException) {
        return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));
    }

    public CompletableFuture<ResponseEntity<PostResponse>> createPostFallback(PostRequest postRequest,
                                                                              RuntimeException runtimeException) {
        return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));
    }
}
