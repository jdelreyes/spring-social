package ca.georgebrown.commentservice.service;

import ca.georgebrown.commentservice.dto.CommentRequest;
import ca.georgebrown.commentservice.dto.CommentResponse;
import ca.georgebrown.commentservice.model.Comment;
import ca.georgebrown.commentservice.repository.CommentRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public Map<String, Object> createComment(CommentRequest commentRequest, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = validateUserIdFromCookie(httpServletRequest);
        if (!(Boolean) stringObjectMap.get("status"))
            return stringObjectMap;

        Comment comment = Comment.builder()
                .content(commentRequest.getContent())
                .postId(commentRequest.getPostId())
                .userId((String) stringObjectMap.get("userId"))
                .build();

        String commentId = commentRepository.save(comment).getId();

        return new HashMap<String, Object>() {{
            put("commentId", commentId);
            put("status", true);
        }};
    }

    @Override
    public CommentResponse getCommentById(String commentId) {
        Comment comment = this.queryComment("id", commentId);

        assert comment != null;
        return mapToCommentResponse(comment);
    }

    @Override
    public boolean updateComment(String commentId, CommentRequest commentRequest) {
        Comment comment = this.queryComment("id", commentId);

        if (comment != null) {
            comment.setContent(commentRequest.getContent());

            commentRepository.save(comment);

            return true;
        }

        return false;
    }

    @Override
    public void deleteComment(String commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentResponse> getUserComments(String userId) {
        List<Comment> commentList = this.queryComments("userId", userId);
        return commentList.stream().map(this::mapToCommentResponse).toList();
    }

    @Override
    public List<CommentResponse> getPostComments(String postId) {
        List<Comment> commentList = this.queryComments("postId", postId);
        return commentList.stream().map(this::mapToCommentResponse).toList();
    }

    @Override
    public List<CommentResponse> getAllComments() {
        return commentRepository.findAll().stream().map(this::mapToCommentResponse).toList();
    }

    private Comment queryComment(String key, Object value) {
        Query query =new Query();
        query.addCriteria(Criteria.where(key).is(value));
        return mongoTemplate.findOne(query, Comment.class);
    }

    private List<Comment> queryComments(String key, Object value) {
        Query query =new Query();
        query.addCriteria(Criteria.where(key).is(value));
        return mongoTemplate.find(query, Comment.class);
    }

    private Map<String, Object> validateUserIdFromCookie(HttpServletRequest httpServletRequest) {
        String userId = getUserIdFromCookie(httpServletRequest);
        if (userId == null)
            return new HashMap<String, Object>(){{put("status", false);put("message", "no logged in user");}};
        return new HashMap<String, Object>(){{put("status", true);put("userId", userId);}};
    }

    private String getUserIdFromCookie(HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("remember-me".equals(cookie.getName())) {
                    // userId value;
                    return cookie.getValue();
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
