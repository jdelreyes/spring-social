package ca.springsocial.commentservice.controller;

import ca.springsocial.commentservice.dto.CommentRequest;
import ca.springsocial.commentservice.model.Comment;
import ca.springsocial.commentservice.repository.CommentRepository;
import ca.springsocial.commentservice.service.CommentServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
public class CommentControllerIntegrationTest {


    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private CommentServiceImpl commentServiceImpl;
    @Mock
    private HttpServletRequest httpServletRequest;

    @Before("1")
    public void setup() {
        commentRepository = Mockito.mock(CommentRepository.class);
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateComment() {
        // Setup
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent("This is a test comment content.");
        commentRequest.setPostId(String.valueOf(1L));

        // Mocking the behavior of validateUserIdFromCookie
        Map<String, Object> validationResponse = new HashMap<>();
        validationResponse.put("status", true);
        validationResponse.put("userId", 1L);
        when(validateUserIdFromCookie(httpServletRequest)).thenReturn(validationResponse);

        // Mocking the behavior of commentRepository.save
        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setPostId(commentRequest.getPostId());
        comment.setUserId(1L);
        when(commentRepository.save(comment)).thenReturn(comment);

        // Execution
        Map<String, Object> result = commentServiceImpl.createComment(commentRequest, httpServletRequest);

        // Assertions
        assertEquals(true, result.get("status"));
        assertEquals(1L, result.get("commentId"));


    }

    private Map<String, Object> validateUserIdFromCookie(HttpServletRequest httpServletRequest) {
        Long userId = getUserIdFromCookie(httpServletRequest);
        if (userId == null)
            return new HashMap<String, Object>() {{
                put("status", false);
                put("message", "no logged in user");
            }};
        return new HashMap<String, Object>() {{
            put("status", true);
            put("userId", userId);
        }};
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
}
