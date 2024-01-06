package ca.springsocial.postservice.service;

import ca.springsocial.postservice.dto.combined.PostWithComments;
import ca.springsocial.postservice.dto.comment.CommentResponse;
import ca.springsocial.postservice.dto.post.PostRequest;
import ca.springsocial.postservice.dto.post.PostResponse;
import ca.springsocial.postservice.dto.user.UserResponse;
import ca.springsocial.postservice.events.PostCreatedEvent;
import ca.springsocial.postservice.model.Post;
import ca.springsocial.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
// helps us log information
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final MongoTemplate mongoTemplate;
    private final WebClient webClient;
    private final KafkaTemplate<String, PostCreatedEvent> kafkaTemplate;

    @Value("${comment.service.url}")
    private String commentServiceUri;
    @Value("${user.service.url}")
    private String userServiceUri;

    @Override
    public ResponseEntity<PostResponse> createPost(PostRequest postRequest) {
        UserResponse userResponse = webClient.get()
                .uri(userServiceUri + "/" + postRequest.getUserId())
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, ex -> ex.getStatusCode().is4xxClientError()
                        ? Mono.empty()
                        : Mono.error(ex))
                .block();

        if (userResponse != null) {
            Post post = Post.builder()
                    .title(postRequest.getTitle())
                    .content(postRequest.getContent())
                    .userId(postRequest.getUserId())
                    .build();

            kafkaTemplate.send("postCreatedEventTopic", new PostCreatedEvent(post.getId(), userResponse.getId()));

            return new ResponseEntity<>(mapToPostResponse(post), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<PostResponse> updatePost(String postId, PostRequest postRequest) {
        Post post = queryPost("id", postId);

        if (post != null) {
            post.setTitle(postRequest.getTitle());
            post.setContent(postRequest.getContent());

            postRepository.save(post);
            return new ResponseEntity<>(mapToPostResponse(post), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override
    public void deletePost(String postId) {
        postRepository.deleteById(postId);
    }

    public PostResponse getPostById(String postId) {
        Post post = queryPost("id", postId);
        if (post != null)
            return mapToPostResponse(post);
        return null;
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
    public ResponseEntity<PostWithComments> getPostWithComments(String postId) {
        List<CommentResponse> commentResponseList = webClient
                .get()
                .uri(commentServiceUri + "?postId=" + postId)
                .retrieve()
                .bodyToFlux(CommentResponse.class)
                .collectList()
//                block to make this synchronous
                .block();

        Post post = this.queryPost("id", postId);
        if (post == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        PostResponse postResponse = mapToPostResponse(post);

        return new ResponseEntity<>(new PostWithComments(postResponse, commentResponseList), HttpStatus.OK);
    }

    //    helper methods
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
