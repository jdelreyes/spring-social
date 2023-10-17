package ca.georgebrown.postservice.service;

import ca.georgebrown.postservice.dto.PostRequest;
import ca.georgebrown.postservice.dto.PostResponse;
import ca.georgebrown.postservice.model.Post;

import java.util.HashMap;
import java.util.List;

public interface PostService {
    //    TODO: update PostServiceImpl and PostController
    HashMap<String,String> createPost(String userId, PostRequest postRequest);

    void updatePost(String postId, PostRequest postRequest);

    void deletePost(String postId);

    PostResponse getPost(String postId);

    List<PostResponse> getAllPosts();

    List<PostResponse> getAllPostsFromUser(String userId);
}
