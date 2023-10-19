package ca.georgebrown.userservice.service;

import ca.georgebrown.userservice.dto.combined.PostWithComments;
import ca.georgebrown.userservice.dto.combined.UserWithComments;
import ca.georgebrown.userservice.dto.combined.UserWithPostsWithComments;
import ca.georgebrown.userservice.dto.comment.CommentResponse;
import ca.georgebrown.userservice.dto.post.PostResponse;
import ca.georgebrown.userservice.dto.combined.UserWithPosts;
import ca.georgebrown.userservice.dto.user.UserRequest;
import ca.georgebrown.userservice.dto.user.UserResponse;
import ca.georgebrown.userservice.model.User;
import ca.georgebrown.userservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    @Autowired
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Map<String, Object> signUp(UserRequest userRequest) {
        Map<String, Object> userHashMap = new HashMap<>();

        if (!isEmailAddress(userRequest.getEmail())) {
            userHashMap.put("status", false);
            userHashMap.put("message", "invalid email address");
            return userHashMap;
        }

        if (userNameExists(userRequest.getUserName())) {
            userHashMap.put("status", false);
            userHashMap.put("message", "userName already in use");
            return userHashMap;
        }

        User user = User.builder()
                .userName(userRequest.getUserName())
                .email(userRequest.getEmail())
                .password(bCryptPasswordEncoder.encode(userRequest.getPassword()))
                .bio(userRequest.getBio())
                .build();

        String userId = userRepository.save(user).getId();
        userHashMap.put("status", true);
        userHashMap.put("userId", userId);

        return userHashMap;
    }

    @Override
    public Map<String, Object> login(String userName, String password, HttpServletResponse response) {
        Map<String, Object> userHashMap = new HashMap<>();
        User user = this.queryUser("userName", userName);

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

        setCookie(response, new HashMap<String, String>() {{
            put("userId", user.getId());
        }});

        userHashMap.put("status", true);
        userHashMap.put("message", "successfully logged in");
        return userHashMap;
    }
    @Override
    public Map<String, Object> logout(HttpServletResponse httpServletResponse) {
        this.removeCookie(httpServletResponse);
        return new HashMap<String, Object>(){{put("message", "successfully logout");put("status", true);}};
    }

    @Override
    public boolean updateUser(String userId, UserRequest userRequest) {
        User user = this.queryUser("id", userId);

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
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserResponse getUserById(String userId) {
        User user =this.queryUser("id", userId);

        if (user != null)
            return mapToUserResponse(user);
        return null;
    }

    @Override
    public UserResponse getUserByUserName(String userName) {
        User user =this.queryUser("userName", userName);

        if (user != null)
            return mapToUserResponse(user);
        return null;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::mapToUserResponse).toList();
    }

    public UserWithPosts getUserWithPosts(String userId) {
        String postServiceUrl = "http://127.0.0.1:8081/api/post/" + userId + "/all";

        ResponseEntity<List<PostResponse>> responseEntity = restTemplate.exchange(
                postServiceUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PostResponse>>() {}
        );

        User user = this.queryUser("id", userId);
        if (user == null) return null;

        List<PostResponse> postResponseList = responseEntity.getBody();
        UserResponse userResponse = mapToUserResponse(user);

        return new UserWithPosts(userResponse, postResponseList);
    }
//    todo
    @Override
    public UserWithPostsWithComments getUserWithPostsWithComments(String userId) {
        String postWithCommentsServiceUrl = "http://127.0.0.1:8081/api/post/" + userId + "/all/posts/comments";

        ResponseEntity<List<PostWithComments>> responseEntity = restTemplate.exchange(
                postWithCommentsServiceUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PostWithComments>>() {}
        );

        User user = this.queryUser("id", userId);
        if (user == null) return null;

        List<PostWithComments> postWithCommentsList = responseEntity.getBody();
        UserResponse userResponse = mapToUserResponse(user);

        return new UserWithPostsWithComments(userResponse, postWithCommentsList);
    }
//    todo
    @Override
    public UserWithComments getUserWithComments(String userId) {
        String commentServiceUrl = "http://127.0.0.1:8082/api/comment/" + userId + "/all";

        ResponseEntity<List<CommentResponse>> responseEntity = restTemplate.exchange(
                commentServiceUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CommentResponse>>() {}
        );

        User user = this.queryUser("id", userId);
        if (user == null) return null;

        List<CommentResponse> commentResponses = responseEntity.getBody();
        UserResponse userResponse = mapToUserResponse(user);

        return new UserWithComments(userResponse, commentResponses);
    }

    private List<User> queryUsers(String key, Object value) {
        Query query = new Query();
        query.addCriteria(Criteria.where(key).is(value));
        return mongoTemplate.find(query, User.class);
    }

    private User queryUser(String key, Object value) {
        Query query = new Query();
        query.addCriteria(Criteria.where(key).is(value));
        return mongoTemplate.findOne(query, User.class);
    }

    private boolean userNameExists(String userName) {
        return this.queryUser("userName", userName) != null;
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

    private void setCookie(HttpServletResponse response, HashMap<String, String> userId) {
        Cookie cookie = new Cookie("remember-me", userId.get("userId"));

//        7 days
        int seconds = 60 * 60 * 24 * 7;
        String rootDirectory = "/";

        cookie.setMaxAge(seconds);
        cookie.setPath(rootDirectory);
        response.addCookie(cookie);
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
