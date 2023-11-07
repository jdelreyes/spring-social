package ca.georgebrown.postservice.service;

import ca.georgebrown.postservice.dto.combined.PostWithComments;
import ca.georgebrown.postservice.dto.post.PostRequest;
import ca.georgebrown.postservice.dto.post.PostResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface PostService {
    Map<String,Object> createPost(PostRequest postRequest, HttpServletRequest httpServletRequest);
    boolean updatePost(String postId, PostRequest postRequest);
    void deletePost(String postId);
    PostResponse getPostById(String postId);
    List<PostResponse> getPosts();
    List<PostResponse> getUserPosts(Long userId);
    PostWithComments getPostWithComments(String postId);
    List<PostWithComments> getPostsWithCommentsByUserId(Long userId);
}
