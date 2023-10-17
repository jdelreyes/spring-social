package ca.georgebrown.postservice.service;

import ca.georgebrown.postservice.dto.combined.PostWithComments;
import ca.georgebrown.postservice.dto.post.PostRequest;
import ca.georgebrown.postservice.dto.post.PostResponse;

import java.util.List;
import java.util.Map;

public interface PostService {
    //    TODO: update PostServiceImpl and PostController
    Map<String,Object> createPost(String userId, PostRequest postRequest);
    boolean updatePost(String postId, PostRequest postRequest);
    void deletePost(String postId);
    PostResponse getPostById(String postId);
    List<PostResponse> getAllPosts();
    List<PostResponse> getUserPosts(String userId);
    PostWithComments getPostWithComments(String postId);
}
