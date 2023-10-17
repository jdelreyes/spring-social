package ca.georgebrown.postservice.service;

import ca.georgebrown.postservice.dto.PostRequest;
import ca.georgebrown.postservice.dto.PostResponse;
import ca.georgebrown.postservice.model.Post;
import ca.georgebrown.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
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
    public HashMap<String, String> createPost(String userId, PostRequest postRequest) {
        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .userId(userId)
                .build();

        String postId = postRepository.save(post).getId();

        HashMap<String, String> postIdMap = new HashMap<>();
        postIdMap.put("postId", postId);

        return postIdMap;
    }

    @Override
    public void updatePost(String postId, PostRequest postRequest) {
        Query query = new Query();

        query.addCriteria(Criteria.where("id").is(postId));
        Post post = mongoTemplate.findOne(query, Post.class);

        if (post != null) {
            post.setTitle(postRequest.getTitle());
            post.setContent(postRequest.getContent());

            postRepository.save(post);
        }
    }

    @Override
    public void deletePost(String postId) {
        postRepository.deleteById(postId);
    }

    public PostResponse getPost(String postId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(postId));

        Post post = mongoTemplate.findOne(query, Post.class);

        assert post != null;
        return mapToPostResponse(post);
    }

    @Override
    public List<PostResponse> getAllPosts() {
        List<Post> postList = postRepository.findAll();

        return postList.stream().map(this::mapToPostResponse).toList();
    }

    @Override
    public List<PostResponse> getAllPostsFromUser(String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));

        List<Post> postList = mongoTemplate.find(query, Post.class);

        return postList.stream().map(this::mapToPostResponse).toList();
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
