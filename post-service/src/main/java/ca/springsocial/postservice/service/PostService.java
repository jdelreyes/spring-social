package ca.springsocial.postservice.service;

import ca.springsocial.postservice.dto.combined.PostWithComments;
import ca.springsocial.postservice.dto.post.PostRequest;
import ca.springsocial.postservice.dto.post.PostResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface PostService {
    Map<String, Object> createPost(PostRequest postRequest);

    boolean updatePost(String postId, PostRequest postRequest);

    void deletePost(String postId);

    PostResponse getPostById(String postId);

    List<PostResponse> getPosts();

    List<PostResponse> getUserPosts(Long userId);

    ResponseEntity<PostWithComments> getPostWithComments(String postId);

}
