package ca.georgebrown.postservice.controller;

import ca.georgebrown.postservice.dto.PostRequest;
import ca.georgebrown.postservice.dto.PostResponse;
import ca.georgebrown.postservice.service.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
    private final PostServiceImpl postService;

//    TODO: change @PostMapping to /create and just read from the userId in the cookie jar :D
//    @PostMapping({"/create"})
    @PostMapping({"/{userId}/create"})
    @ResponseStatus(HttpStatus.CREATED)
    public HashMap<String, String> createPost(@PathVariable("userId") String userId,
                                              @RequestBody PostRequest postRequest) {
        return postService.createPost(userId, postRequest);
    }

    @PutMapping({"/update/{postId}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePost(@PathVariable("postId") String postId,
                           @RequestBody PostRequest postRequest) {
        postService.updatePost(postId, postRequest);
    }

    @DeleteMapping({"/delete/{postId}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable("postId") String postId) {
        postService.deletePost(postId);
    }

    @GetMapping({"/{postId}/details"})
    @ResponseStatus(HttpStatus.OK)
    public PostResponse getPost(@PathVariable("postId") String postId) {
        return postService.getPost(postId);
    }

    @GetMapping({"/all"})
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponse> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping({"/{userId}/all"})
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponse> getAllPostsFromUser(@PathVariable("userId") String userId) {
        return postService.getAllPostsFromUser(userId);
    }
}
