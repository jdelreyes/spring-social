package ca.georgebrown.commentservice.controller;

import ca.georgebrown.commentservice.dto.CommentRequest;
import ca.georgebrown.commentservice.dto.CommentResponse;
import ca.georgebrown.commentservice.service.CommentServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentServiceImpl commentService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createComment(@RequestBody CommentRequest commentRequest, HttpServletRequest httpServletRequest) {
        return new ResponseEntity<>(commentService.createComment(commentRequest, httpServletRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{commentId}/details")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponse getCommentById(@PathVariable String commentId) {
        return commentService.getCommentById(commentId);
    }

    @PutMapping("/update/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable String commentId, CommentRequest commentRequest) {
        boolean isCommentUpdated = commentService.updateComment(commentId, commentRequest);
        if (!isCommentUpdated)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/delete/{commentId}")
    public void deleteComment(@PathVariable String commentId){
        commentService.deleteComment(commentId);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponse> getAllComments() {
        return commentService.getAllComments();
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    List<CommentResponse> getUserComments(@PathVariable String userId) {
        return commentService.getUserComments(userId);
    }

    @GetMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    List<CommentResponse> getPostComments(@PathVariable String postId) {
        return commentService.getPostComments(postId);
    }
}
