package ca.springsocial.commentservice.service;

import ca.springsocial.commentservice.dto.comment.CommentRequest;
import ca.springsocial.commentservice.dto.comment.CommentResponse;
import ca.springsocial.commentservice.dto.post.PostResponse;
import ca.springsocial.commentservice.dto.user.UserResponse;
import ca.springsocial.commentservice.events.CommentCreatedEvent;
import ca.springsocial.commentservice.model.Comment;
import ca.springsocial.commentservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final WebClient webClient;
    private final KafkaTemplate<String, CommentCreatedEvent> kafkaTemplate;

    @Value("${post.service.url}")
    private String postServiceUri;
    @Value("${user.service.url}")
    private String userServiceUri;

    @Override
    public ResponseEntity<CommentResponse> createComment(CommentRequest commentRequest) {
        PostResponse postResponse = webClient.get()
                .uri(postServiceUri + "/" + commentRequest.getPostId())
                .retrieve()
                .bodyToMono(PostResponse.class)
                .onErrorResume(WebClientResponseException.class, ex -> ex.getStatusCode().is4xxClientError()
                        ? Mono.empty()
                        : Mono.error(ex))
                .block();

        UserResponse userResponse = webClient.get()
                .uri(userServiceUri + "/" + commentRequest.getUserId())
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, ex -> ex.getStatusCode().is4xxClientError()
                        ? Mono.empty()
                        : Mono.error(ex))
                .block();

        if (postResponse == null || userResponse == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setPostId(postResponse.getId());
        comment.setUserId(userResponse.getId());

        commentRepository.save(comment);
        kafkaTemplate.send("commentCreatedEventTopic", new CommentCreatedEvent(comment.getId(),
                comment.getUserId(), comment.getPostId()));

        return new ResponseEntity<>(mapToCommentResponse(comment), HttpStatus.CREATED);

    }

    @Override
    public CommentResponse getCommentById(Long commentId) {
        Comment comment = commentRepository.findCommentById(commentId);
        if (comment != null)
            return mapToCommentResponse(comment);
        return null;
    }

    @Override
    public ResponseEntity<CommentResponse> updateComment(Long commentId, CommentRequest commentRequest) {
        Comment comment = commentRepository.findCommentById(commentId);
        if (comment != null) {
            comment.setContent(commentRequest.getContent());
            return new ResponseEntity<>(mapToCommentResponse(comment), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentResponse> getUserComments(Long userId) {
        List<Comment> commentList = commentRepository.findCommentsByUserId(userId);
        return commentList.stream().map(this::mapToCommentResponse).toList();
    }

    @Override
    public List<CommentResponse> getPostComments(String postId) {
        List<Comment> commentList = commentRepository.findCommentsByPostId(postId);
        return commentList.stream().map(this::mapToCommentResponse).toList();
    }

    @Override
    public List<CommentResponse> getCommentsByUserIdAndPostId(Long userId, String postId) {
        List<Comment> commentList = commentRepository.findCommentsByUserIdAndPostId(userId, postId);
        return commentList.stream().map(this::mapToCommentResponse).toList();
    }

    @Override
    public List<CommentResponse> getComments() {
        return commentRepository.findAll().stream().map(this::mapToCommentResponse).toList();
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .dateTimeCommented(comment.getDateTimeCommented())
                .postId(comment.getPostId())
                .userId(comment.getUserId())
                .build();
    }
}
