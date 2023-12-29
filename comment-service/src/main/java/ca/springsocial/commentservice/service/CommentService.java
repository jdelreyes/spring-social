package ca.springsocial.commentservice.service;

import ca.springsocial.commentservice.dto.comment.CommentRequest;
import ca.springsocial.commentservice.dto.comment.CommentResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CommentService {
    ResponseEntity<CommentResponse> createComment(CommentRequest commentRequest);

    ResponseEntity<CommentResponse> updateComment(Long commentId, CommentRequest commentRequest);

    void deleteComment(Long commentId);

    CommentResponse getCommentById(Long commentId);

    List<CommentResponse> getComments();

    List<CommentResponse> getUserComments(Long userId);

    List<CommentResponse> getPostComments(String postId);

    List<CommentResponse> getCommentsByUserIdAndPostId(Long userId, String postId);
}
