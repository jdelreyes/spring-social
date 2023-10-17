package ca.georgebrown.userservice.service;

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
    boolean updateUser(String userId, UserRequest userRequest);
    void deleteUser(String userId);
    UserResponse getUserById(String userId);
    UserResponse getUserByUserName(String userName);
    List<UserResponse> getAllUsers();
    UserWithPosts getUserWithPosts(String userId);
    UserWithPostsWithComments getUserWithPostsWithComments(String userId);
}
