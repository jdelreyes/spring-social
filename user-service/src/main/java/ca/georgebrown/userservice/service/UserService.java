package ca.georgebrown.userservice.service;

import ca.georgebrown.userservice.dto.user.UserRequest;
import ca.georgebrown.userservice.dto.user.UserResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.util.Pair;

import java.util.HashMap;
import java.util.List;

public interface UserService {
    Pair<HashMap<String, String>, Boolean> createUser(UserRequest userRequest);
    Pair<HashMap<String, String>, Boolean> login(String userName, String password, HttpServletResponse response);
    HashMap<String, String> logout();
    UserResponse updateUser(String userId, UserRequest userRequest);
    void deleteUser(String userId);
    UserResponse getUserById(String userId);
    UserResponse getUserByUserName(String userName);
    List<UserResponse> getAllUsers();
}
