package ca.georgebrown.userservice.service;

import ca.georgebrown.userservice.dto.combined.UserWithComments;
import ca.georgebrown.userservice.dto.combined.UserWithPosts;
import ca.georgebrown.userservice.dto.combined.UserWithPostsWithComments;
import ca.georgebrown.userservice.dto.user.UserRequest;
import ca.georgebrown.userservice.dto.user.UserResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

public interface UserService {
    Map<String, Object> signUp(UserRequest userRequest);
    Map<String, Object> login(String userName, String password, HttpServletResponse response);
    Map<String, Object> logout(HttpServletResponse httpServletResponse);
    boolean updateUser(Long userId, UserRequest userRequest);
    void deleteUser(Long userId);
    UserResponse getUserById(Long userId);
    List<UserResponse> getUsers();
    UserWithPosts getUserWithPosts(Long userId);
    UserWithPostsWithComments getUserWithPostsWithComments(Long userId);
    UserWithComments getUserWithComments(Long userId);
}
