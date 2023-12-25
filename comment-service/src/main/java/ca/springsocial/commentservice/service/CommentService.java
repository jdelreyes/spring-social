package ca.springsocial.commentservice.service;

import ca.springsocial.commentservice.dto.comment.CommentRequest;
import ca.springsocial.commentservice.dto.comment.CommentResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CommentService {
    ResponseEntity<?> createComment(CommentRequest commentRequest);

    CommentResponse updateComment(Long commentId, CommentRequest commentRequest);

    void deleteComment(Long commentId);

    CommentResponse getCommentById(Long commentId);

    List<CommentResponse> getComments();

    List<CommentResponse> getUserComments(Long userId);

    List<CommentResponse> getPostComments(String postId);

    List<CommentResponse> getCommentsByUserIdAndPostId(Long userId, String postId);
}
