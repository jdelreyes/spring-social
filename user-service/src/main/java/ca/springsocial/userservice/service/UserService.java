package ca.springsocial.userservice.service;

import ca.springsocial.userservice.dto.combined.UserWithComments;
import ca.springsocial.userservice.dto.combined.UserWithPosts;
import ca.springsocial.userservice.dto.user.UserRequest;
import ca.springsocial.userservice.dto.user.UserResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    ResponseEntity<UserResponse> createUser(UserRequest userRequest);

    ResponseEntity<UserResponse> updateUser(Long userId, UserRequest userRequest);

    void deleteUser(Long userId);

    UserResponse getUserById(Long userId);

    List<UserResponse> getUsers();

    ResponseEntity<UserWithPosts> getUserWithPosts(Long userId);

    ResponseEntity<UserWithComments> getUserWithComments(Long userId);
}
