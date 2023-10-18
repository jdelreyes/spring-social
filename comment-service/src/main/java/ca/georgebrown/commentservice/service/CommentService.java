package ca.georgebrown.commentservice.service;

import ca.georgebrown.commentservice.dto.CommentRequest;
import ca.georgebrown.commentservice.dto.CommentResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface CommentService {
    Map<String, Object> createComment(CommentRequest commentRequest, HttpServletRequest httpServletRequest);
    CommentResponse getCommentById(String commentId);
    boolean updateComment(String commentId, CommentRequest commentRequest);
    void deleteComment(String commentId);
    List<CommentResponse> getAllComments();
    List<CommentResponse> getUserComments(String userId);
    List<CommentResponse> getPostComments(String postId);
}
