package ca.springsocial.commentservice.service;

import ca.springsocial.commentservice.dto.comment.CommentRequest;
import ca.springsocial.commentservice.dto.comment.CommentResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CommentService {
    ResponseEntity<Map<String, Object>> createComment(CommentRequest commentRequest, HttpServletRequest httpServletRequest);

    CommentResponse getCommentById(Long commentId);

    CommentResponse updateComment(Long commentId, CommentRequest commentRequest);

    void deleteComment(Long commentId);

    List<CommentResponse> getComments();

    List<CommentResponse> getUserComments(Long userId);

    List<CommentResponse> getPostComments(String postId);

    List<CommentResponse> getCommentsByUserIdAndPostId(Long userId, String postId);
}
