package ca.springsocial.commentservice.controller;

import ca.springsocial.commentservice.dto.comment.CommentRequest;
import ca.springsocial.commentservice.dto.comment.CommentResponse;
import ca.springsocial.commentservice.service.CommentServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentServiceImpl commentService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createComment(@RequestBody CommentRequest commentRequest, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectHashMap = commentService.createComment(commentRequest, httpServletRequest);
        if ((Boolean) stringObjectHashMap.get("status"))
            return new ResponseEntity<>(stringObjectHashMap, HttpStatus.CREATED);
        return new ResponseEntity<>(stringObjectHashMap, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable Long commentId) {
        CommentResponse commentResponse = commentService.getCommentById(commentId);
        if (commentResponse != null)
            return new ResponseEntity<>(commentResponse, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId, @RequestBody CommentRequest commentRequest) {
        CommentResponse commentResponse = commentService.updateComment(commentId, commentRequest);
        if (commentResponse != null)
            return new ResponseEntity<>(commentResponse, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponse> getComments(@RequestParam(name = "userId") Optional<Long> userId,
                                             @RequestParam(name = "postId") Optional<String> postId) {
        if (userId.isPresent() && postId.isPresent())
            return commentService.getCommentsByUserIdAndPostId(userId.get(), postId.get());
        if (userId.isPresent()) return commentService.getUserComments(userId.get());
        if (postId.isPresent()) return commentService.getPostComments(postId.get());
        return commentService.getComments();
    }
}
