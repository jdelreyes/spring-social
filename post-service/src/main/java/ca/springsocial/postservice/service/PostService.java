package ca.springsocial.postservice.service;

import ca.springsocial.postservice.dto.combined.PostWithComments;
import ca.springsocial.postservice.dto.post.PostRequest;
import ca.springsocial.postservice.dto.post.PostResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface PostService {
    Map<String, Object> createPost(PostRequest postRequest, HttpServletRequest httpServletRequest);

    boolean updatePost(String postId, PostRequest postRequest);

    void deletePost(String postId);

    PostResponse getPostById(String postId);

    List<PostResponse> getPosts();

    List<PostResponse> getUserPosts(Long userId);

    PostWithComments getPostWithComments(String postId);

}
