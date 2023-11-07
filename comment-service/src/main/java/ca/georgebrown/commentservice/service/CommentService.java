package ca.georgebrown.commentservice.service;

import ca.georgebrown.commentservice.dto.CommentRequest;
import ca.georgebrown.commentservice.dto.CommentResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface CommentService {
    Map<String, Object> createComment(CommentRequest commentRequest, HttpServletRequest httpServletRequest);
    CommentResponse getCommentById(Long commentId);
    boolean updateComment(Long commentId, CommentRequest commentRequest);
    void deleteComment(Long commentId);
    List<CommentResponse> getComments();
    List<CommentResponse> getUserComments(Long userId);
    List<CommentResponse> getPostComments(String postId);
}
