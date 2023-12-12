package ca.springsocial.commentservice.service;

import ca.springsocial.commentservice.dto.comment.CommentRequest;
import ca.springsocial.commentservice.dto.comment.CommentResponse;
import ca.springsocial.commentservice.dto.post.PostResponse;
import ca.springsocial.commentservice.model.Comment;
import ca.springsocial.commentservice.repository.CommentRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final WebClient webClient;

    // todo: to check if post exists
    @Value("${post.service.url}")
    private String postServiceUri;

    @Override
    public Map<String, Object> createComment(CommentRequest commentRequest, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = validateUserIdFromCookie(httpServletRequest);
        if (!(Boolean) stringObjectMap.get("status"))
            return stringObjectMap;

        PostResponse postResponse = webClient.get()
                .uri(postServiceUri + "/" + commentRequest.getPostId())
                .retrieve()
                .bodyToMono(PostResponse.class)
                .onErrorResume(WebClientResponseException.class, ex -> ex.getRawStatusCode() == 400
                        ? Mono.empty()
                        : Mono.error(ex))
                .block();

        if (postResponse != null) {
            Comment comment = new Comment();
            comment.setContent(commentRequest.getContent());
            comment.setPostId(postResponse.getId());
            comment.setUserId((Long) stringObjectMap.get("userId"));

            Long commentId = commentRepository.save(comment).getId();

            return new HashMap<String, Object>() {{
                put("commentId", commentId);
                put("status", true);
            }};
        }
        return new HashMap<String, Object>() {{
            put("message", "post does not exist");
            put("status", false);
        }};

    }

    @Override
    public CommentResponse getCommentById(Long commentId) {
        Comment comment = commentRepository.findCommentById(commentId);
        if (comment != null)
            return mapToCommentResponse(comment);
        return null;
    }

    @Override
    public CommentResponse updateComment(Long commentId, CommentRequest commentRequest) {
        Comment comment = commentRepository.findCommentById(commentId);
        if (comment != null) {
            comment.setContent(commentRequest.getContent());
            Comment savedComment = commentRepository.save(comment);

            return mapToCommentResponse(savedComment);
        }

        return null;
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

    private Map<String, Object> validateUserIdFromCookie(HttpServletRequest httpServletRequest) {
        Long userId = getUserIdFromCookie(httpServletRequest);
        if (userId == null)
            return new HashMap<String, Object>() {{
                put("status", false);
                put("message", "no logged in user");
            }};
        return new HashMap<String, Object>() {{
            put("status", true);
            put("userId", userId);
        }};
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
