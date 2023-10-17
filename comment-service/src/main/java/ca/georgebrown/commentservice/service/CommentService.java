package ca.georgebrown.commentservice.service;

import ca.georgebrown.commentservice.dto.CommentRequest;
import ca.georgebrown.commentservice.dto.CommentResponse;

import java.util.HashMap;
import java.util.List;

public interface CommentService {
    HashMap<String,String> createComment(CommentRequest commentRequest);
    CommentResponse getComment(String commentId);
    void updateComment(String commentId, CommentRequest commentRequest);
    void deleteComment(String commentId);
    List<CommentResponse> getAllCommentsFromUser(String userId);
    List<CommentResponse> getAllComments();
}
