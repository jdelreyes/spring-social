package ca.springsocial.postservice.service;

import ca.springsocial.postservice.dto.combined.PostWithComments;
import ca.springsocial.postservice.dto.comment.CommentResponse;
import ca.springsocial.postservice.dto.post.PostRequest;
import ca.springsocial.postservice.dto.post.PostResponse;
import ca.springsocial.postservice.model.Post;
import ca.springsocial.postservice.repository.PostRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    private RestTemplate restTemplate;

    @Value("${comment.service.url}")
    private String commentServiceUri;

    @Override
    public Map<String, Object> createPost(PostRequest postRequest, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = validateUserIdFromCookie(httpServletRequest);
        if (!(Boolean) stringObjectMap.get("status"))
            return stringObjectMap;

        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .userId((Long) stringObjectMap.get("userId"))
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
    public List<PostResponse> getPosts() {
        List<Post> postList = postRepository.findAll();
        return postList.stream().map(this::mapToPostResponse).toList();
    }

    @Override
    public List<PostResponse> getUserPosts(Long userId) {
        List<Post> postList = queryPosts("userId", userId);
        return postList.stream().map(this::mapToPostResponse).toList();
    }

    @Override
    public PostWithComments getPostWithComments(String postId) {
        String commentServiceUrl = "http://127.0.0.1:8082/api/comments/post/" + postId;

        ResponseEntity<List<CommentResponse>> responseEntity = restTemplate.exchange(
                commentServiceUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CommentResponse>>() {}
        );

        Post post = this.queryPost("id", postId);
        if (post == null) return null;

        List<CommentResponse> commentResponseList = responseEntity.getBody();
        PostResponse postResponse = mapToPostResponse(post);

        return new PostWithComments(postResponse, commentResponseList);
    }

    @Override
    public List<PostWithComments> getPostsWithCommentsByUserId(Long userId) {
        // getting list of post with user id
        List<Post> postList = this.queryPosts("userId", userId);
        List<PostWithComments> postWithCommentsList = new ArrayList<>();

        for (Post post : postList) {
            String commentServiceUrl = "http://127.0.0.1:8082/api/comments/post/" + post.getId();

            ResponseEntity<List<CommentResponse>> responseEntity = restTemplate.exchange(
                    commentServiceUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CommentResponse>>() {
                    }
            );
            PostResponse postResponse = mapToPostResponse(post);

            assert false;
            postWithCommentsList.add(new PostWithComments(postResponse, responseEntity.getBody()));

        }

        return postWithCommentsList;
    }

    public Post queryPost(String key, Object value) {
        Query query = new Query();
        query.addCriteria(Criteria.where(key).is(value));
        return mongoTemplate.findOne(query, Post.class);
    }

    private List<Post> queryPosts(String key, Object value) {
        Query query = new Query();
        query.addCriteria(Criteria.where(key).is(value));
        return mongoTemplate.find(query, Post.class);
    }

    private Map<String, Object> validateUserIdFromCookie(HttpServletRequest httpServletRequest) {
        Long userId = getUserIdFromCookie(httpServletRequest);
        if (userId == null)
            return new HashMap<String, Object>(){{put("status", false);put("message", "no logged in user");}};
        return new HashMap<String, Object>(){{put("status", true);put("userId", userId);}};
    }

    private Long getUserIdFromCookie(HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("remember-me".equals(cookie.getName())) {
                    // userId value;
                    return Long.parseLong(cookie.getValue());
                }
            }
        }

        return null;
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
