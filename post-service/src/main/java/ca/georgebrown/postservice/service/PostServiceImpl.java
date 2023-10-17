package ca.georgebrown.postservice.service;

import ca.georgebrown.postservice.dto.combined.PostWithComments;
import ca.georgebrown.postservice.dto.post.PostRequest;
import ca.georgebrown.postservice.dto.post.PostResponse;
import ca.georgebrown.postservice.model.Post;
import ca.georgebrown.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
// helps us log information
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public Map<String, Object> createPost(String userId, PostRequest postRequest) {
        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .userId(userId)
                .build();

        String postId = postRepository.save(post).getId();

        Map<String, Object> postHashMap = new HashMap<>();
        postHashMap.put("postId", postId);
        postHashMap.put("status", true);

        return postHashMap;
    }

    @Override
    public boolean updatePost(String postId, PostRequest postRequest) {
        Post post = queryPost("id", postId);

        if (post != null) {
            post.setTitle(postRequest.getTitle());
            post.setContent(postRequest.getContent());

            postRepository.save(post);

            return true;
        }

        return false;
    }

    @Override
    public void deletePost(String postId) {
        postRepository.deleteById(postId);
    }

    public PostResponse getPostById(String postId) {
        Post post = queryPost("id", postId);
        return mapToPostResponse(post);
    }

    @Override
    public List<PostResponse> getAllPosts() {
        List<Post> postList = postRepository.findAll();
        return postList.stream().map(this::mapToPostResponse).toList();
    }

    @Override
    public List<PostResponse> getUserPosts(String userId) {
        List<Post> postList = queryPosts("userId", userId);
        return postList.stream().map(this::mapToPostResponse).toList();
    }
//todo
    @Override
    public PostWithComments getPostWithComments(String postId) {
        return null;
    }

    private Post queryPost(String key, Object value) {
        Query query = new Query();
        query.addCriteria(Criteria.where(key).is(value));
        return mongoTemplate.findOne(query, Post.class);
    }

    private List<Post> queryPosts(String key, Object value) {
        Query query = new Query();
        query.addCriteria(Criteria.where(key).is(value));
        return mongoTemplate.find(query, Post.class);
    }

    private PostResponse mapToPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .dateTimePosted(post.getDateTimePosted())
                .userId(post.getUserId())
                .build();
    }
}
