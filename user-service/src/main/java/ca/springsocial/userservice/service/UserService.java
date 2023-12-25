package ca.springsocial.userservice.service;

import ca.springsocial.userservice.dto.combined.UserWithComments;
import ca.springsocial.userservice.dto.combined.UserWithPosts;
import ca.springsocial.userservice.dto.user.UserRequest;
import ca.springsocial.userservice.dto.user.UserResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserService {
    Map<String, Object> createUser(UserRequest userRequest);

    boolean updateUser(Long userId, UserRequest userRequest);

    void deleteUser(Long userId);

    UserResponse getUserById(Long userId);

    List<UserResponse> getUsers();

    ResponseEntity<UserWithPosts> getUserWithPosts(Long userId);

    ResponseEntity<UserWithComments> getUserWithComments(Long userId);
}
