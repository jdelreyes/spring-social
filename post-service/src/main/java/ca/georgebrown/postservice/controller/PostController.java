package ca.georgebrown.postservice.controller;

import ca.georgebrown.postservice.dto.combined.PostWithComments;
import ca.georgebrown.postservice.dto.post.PostRequest;
import ca.georgebrown.postservice.dto.post.PostResponse;
import ca.georgebrown.postservice.service.PostServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostServiceImpl postService;

    @PostMapping({"/create"})
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createPost(@RequestBody PostRequest postRequest, HttpServletRequest httpServletRequest) {
        return postService.createPost(postRequest, httpServletRequest);
    }

    @PutMapping("/update/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> updatePost(@PathVariable("postId") String postId, @RequestBody PostRequest postRequest) {
        boolean isPostUpdated = postService.updatePost(postId, postRequest);
        if (!isPostUpdated) {
            return ResponseEntity.badRequest().body("Failed to update post with id: " + postId);
        }
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping({"/delete/{postId}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable("postId") String postId) {
        postService.deletePost(postId);
    }

    @GetMapping({"/{postId}/details"})
    @ResponseStatus(HttpStatus.OK)
    public PostResponse getPost(@PathVariable("postId") String postId) {
        return postService.getPostById(postId);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponse> getPosts() {
        return postService.getPosts();
    }


    @GetMapping({"/user/{userId}"})
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponse> getUserPosts(@PathVariable("userId") Long userId) {
        return postService.getUserPosts(userId);
    }

    @GetMapping("/{postId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public PostWithComments getPostWithComments(@PathVariable String postId) {
        return postService.getPostWithComments(postId);
    }

    @GetMapping("/user/{userId}/posts/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<PostWithComments> getPostsWithCommentsByUserId(@PathVariable Long userId) {
        return postService.getPostsWithCommentsByUserId(userId);
    }
}
