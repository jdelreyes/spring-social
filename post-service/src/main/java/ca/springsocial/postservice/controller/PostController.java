package ca.springsocial.postservice.controller;

import ca.springsocial.postservice.dto.combined.PostWithComments;
import ca.springsocial.postservice.dto.post.PostRequest;
import ca.springsocial.postservice.dto.post.PostResponse;
import ca.springsocial.postservice.service.PostServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostServiceImpl postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createPost(@RequestBody PostRequest postRequest, HttpServletRequest httpServletRequest) {
        return postService.createPost(postRequest, httpServletRequest);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable("postId") String postId, @RequestBody PostRequest postRequest) {
        boolean isPostUpdated = postService.updatePost(postId, postRequest);
        if (!isPostUpdated) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping({"/{postId}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable("postId") String postId) {
        postService.deletePost(postId);
    }

    @GetMapping({"/{postId}"})
    @ResponseStatus(HttpStatus.OK)
    public PostResponse getPost(@PathVariable("postId") String postId) {
        return postService.getPostById(postId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponse> getPosts(@RequestParam("userId") Optional<Long> userId) {
        if (userId.isPresent())
            return postService.getUserPosts(userId.get());
        return postService.getPosts();
    }

    @GetMapping({"/{postId}/comments"})
    @ResponseStatus(HttpStatus.OK)
    public PostWithComments getPostWithComments(@PathVariable("postId") String postId) {
        return postService.getPostWithComments(postId);
    }

    @GetMapping({"/user/{userId}/comments"})
    @ResponseStatus(HttpStatus.OK)
    public List<PostWithComments> getPostsWithCommentsByUserId(@PathVariable("userId") Long userId) {
        return postService.getPostsWithCommentsByUserId(userId);
    }
}
