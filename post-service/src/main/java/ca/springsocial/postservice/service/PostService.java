package ca.springsocial.postservice.service;

import ca.springsocial.postservice.dto.combined.PostWithComments;
import ca.springsocial.postservice.dto.post.PostRequest;
import ca.springsocial.postservice.dto.post.PostResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PostService {
    ResponseEntity<PostResponse> createPost(PostRequest postRequest);

    ResponseEntity<PostResponse> updatePost(String postId, PostRequest postRequest);

    void deletePost(String postId);

    PostResponse getPostById(String postId);

    List<PostResponse> getPosts();

    List<PostResponse> getUserPosts(Long userId);

    ResponseEntity<PostWithComments> getPostWithComments(String postId);

}
