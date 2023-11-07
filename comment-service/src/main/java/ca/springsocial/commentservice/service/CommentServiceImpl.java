package ca.springsocial.commentservice.service;

import ca.springsocial.commentservice.dto.CommentRequest;
import ca.springsocial.commentservice.dto.CommentResponse;
import ca.springsocial.commentservice.model.Comment;
import ca.springsocial.commentservice.repository.CommentRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Override
    public Map<String, Object> createComment(CommentRequest commentRequest, HttpServletRequest httpServletRequest) {
        Map<String, Object> stringObjectMap = validateUserIdFromCookie(httpServletRequest);
        if (!(Boolean) stringObjectMap.get("status"))
            return stringObjectMap;

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setPostId(commentRequest.getPostId());
        comment.setUserId((Long) stringObjectMap.get("userId"));

        Long commentId = commentRepository.save(comment).getId();

        return new HashMap<String, Object>() {{
            put("commentId", commentId);
            put("status", true);
        }};
    }

    @Override
    public CommentResponse getCommentById(Long commentId) {
        Comment comment = commentRepository.getCommentById(commentId);

        assert comment != null;
        return mapToCommentResponse(comment);
    }

    @Override
    public boolean updateComment(Long commentId, CommentRequest commentRequest) {
        Comment comment = commentRepository.getCommentById(commentId);

        if (comment != null) {
            comment.setContent(commentRequest.getContent());

            commentRepository.save(comment);

            return true;
        }

        return false;
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentResponse> getUserComments(Long userId) {
        List<Comment> commentList = commentRepository.getCommentsByUserId(userId);
        return commentList.stream().map(this::mapToCommentResponse).toList();
    }

    @Override
    public List<CommentResponse> getPostComments(String postId) {
        List<Comment> commentList = commentRepository.getCommentsByPostId(postId);
        return commentList.stream().map(this::mapToCommentResponse).toList();
    }

    @Override
    public List<CommentResponse> getComments() {
        return commentRepository.findAll().stream().map(this::mapToCommentResponse).toList();
    }

    private Map<String, Object> validateUserIdFromCookie(HttpServletRequest httpServletRequest) {
        Long userId = getUserIdFromCookie(httpServletRequest);
        if (userId == null)
            return new HashMap<String, Object>(){{put("status", false);put("message", "no logged in user");}};
        return new HashMap<String, Object>(){{put("status", true);put("userId", userId);}};
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
