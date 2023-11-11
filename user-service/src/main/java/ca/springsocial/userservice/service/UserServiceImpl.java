package ca.springsocial.userservice.service;

import ca.springsocial.userservice.dto.combined.PostWithComments;
import ca.springsocial.userservice.dto.combined.UserWithComments;
import ca.springsocial.userservice.dto.combined.UserWithPosts;
import ca.springsocial.userservice.dto.combined.UserWithPostsWithComments;
import ca.springsocial.userservice.dto.comment.CommentResponse;
import ca.springsocial.userservice.dto.post.PostResponse;
import ca.springsocial.userservice.dto.user.UserRequest;
import ca.springsocial.userservice.dto.user.UserResponse;
import ca.springsocial.userservice.model.User;
import ca.springsocial.userservice.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final WebClient webClient;

    @Value("${comment.service.url}")
    private String commentServiceUri;
    @Value("${post.service.url}")
    private String postServiceUri;

    @Override
    public Map<String, Object> signUp(UserRequest userRequest) {
        Map<String, Object> userHashMap = new HashMap<>();

        if (!isEmailAddress(userRequest.getEmail())) {
            userHashMap.put("status", false);
            userHashMap.put("message", "invalid email address");
            return userHashMap;
        }

        if (userRepository.findUserByUserName(userRequest.getUserName()) != null) {
            userHashMap.put("status", false);
            userHashMap.put("message", "userName already in use");
            return userHashMap;
        }

        // make user
        User user = new User();
        user.setUserName(userRequest.getUserName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));
        user.setBio(userRequest.getBio());

        Long userId = userRepository.save(user).getId();
        userHashMap.put("status", true);
        userHashMap.put("userId", userId);

        return userHashMap;
    }

    @Override
    public Map<String, Object> login(String userName, String password, HttpServletResponse response) {
        Map<String, Object> userHashMap = new HashMap<>();
        User user = userRepository.findUserByUserName(userName);

        if (user == null) {
            userHashMap.put("status", false);
            userHashMap.put("message", "username and/or password do not match");
            return userHashMap;
        }
        if (!passwordMatches(password, user.getPassword())) {
            userHashMap.put("status", false);
            userHashMap.put("message", "username and/or password do not match");
            return userHashMap;
        }

        setCookie(response, new HashMap<String, Long>() {{
            put("userId", user.getId());
        }});

        userHashMap.put("status", true);
        userHashMap.put("message", "successfully logged in");
        return userHashMap;
    }

    @Override
    public Map<String, Object> logout(HttpServletResponse httpServletResponse) {
        this.removeCookie(httpServletResponse);
        return new HashMap<String, Object>() {{
            put("message", "successfully logout");
            put("status", true);
        }};
    }

    @Override
    public boolean updateUser(Long userId, UserRequest userRequest) {
        User user = userRepository.findUserById(userId);

        if (user != null) {
            user.setUserName(userRequest.getUserName());
            user.setEmail(userRequest.getEmail());
            user.setBio(userRequest.getBio());

            userRepository.save(user);

            return true;
        }
        return false;
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findUserById(userId);

        if (user != null)
            return mapToUserResponse(user);
        return null;
    }

    @Override
    public List<UserResponse> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::mapToUserResponse).toList();
    }

    @Override
    public UserWithPosts getUserWithPosts(Long userId) {
        List<PostResponse> postResponseList = webClient
                .get()
                .uri(postServiceUri + "/user/" + userId)
                .retrieve()
                .bodyToFlux(PostResponse.class)
                .collectList()
//                block to make this synchronous
                .block();

        User user = userRepository.findUserById(userId);
        if (user == null) return null;

        UserResponse userResponse = mapToUserResponse(user);

        return new UserWithPosts(userResponse, postResponseList);
    }

    @Override
    public UserWithPostsWithComments getUserWithPostsWithComments(Long userId) {
        List<PostWithComments> postWithCommentsList = webClient
                .get()
                .uri(postServiceUri + "/user/" + userId + "/posts/comments")
                .retrieve()
                .bodyToFlux(PostWithComments.class)
                .collectList()
//                block to make this synchronous
                .block();

        User user = userRepository.findUserById(userId);
        if (user == null) return null;

        UserResponse userResponse = mapToUserResponse(user);
        return new UserWithPostsWithComments(userResponse, postWithCommentsList);
    }

    @Override
    public UserWithComments getUserWithComments(Long userId) {
        List<CommentResponse> commentResponseList = webClient
                .get()
                .uri(commentServiceUri + "/user/" + userId)
                .retrieve()
                .bodyToFlux(CommentResponse.class)
                .collectList()
//                block to make this synchronous
                .block();

        User user = userRepository.findUserById(userId);
        if (user == null) return null;

        UserResponse userResponse = mapToUserResponse(user);

        return new UserWithComments(userResponse, commentResponseList);
    }

    private boolean isEmailAddress(String email) {
        return Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"" +
                        "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x" +
                        "0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0" +
                        "-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2" +
                        "(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0" +
                        "b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
                .matcher(email)
                .matches();
    }

    private boolean passwordMatches(String password, String HashedPassword) {
        return bCryptPasswordEncoder.matches(password, HashedPassword);
    }

    private void setCookie(HttpServletResponse response, HashMap<String, Long> userId) {
        Cookie cookie = new Cookie("remember-me", userId.get("userId").toString());

//        7 days
        int seconds = 60 * 60 * 24 * 7;
        String rootDirectory = "/";

        cookie.setMaxAge(seconds);
        cookie.setPath(rootDirectory);
        response.addCookie(cookie);
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

    public void removeCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("remember-me", null);
        cookie.setMaxAge(0); // Set the expiration time to zero (in the past)
        response.addCookie(cookie);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .dateTimeJoined(user.getDateTimeJoined())
                .email(user.getEmail())
                .bio(user.getBio())
                .build();
    }
}
