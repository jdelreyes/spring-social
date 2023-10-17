package ca.georgebrown.userservice.service;

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
import org.springframework.data.util.Pair;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private RestTemplate restTemplate;
//    todo
//    private ObjectMapper objectMapper;
//    TODO: initiate a session
//    private final SessionRepository sessionRepository;

    @Override
    public Pair<HashMap<String, String>, Boolean> createUser(UserRequest userRequest) {
        HashMap<String, String> userHashMap = new HashMap<>();

        if (!isEmailAddress(userRequest.getEmail())) {
            userHashMap.put("message", "invalid email address");
            return Pair.of(userHashMap, false);
        }

        if (userNameExists(userRequest.getUserName())) {
            userHashMap.put("message", "userName already in use");
            return Pair.of(userHashMap, false);
        }

        User user = User.builder()
                .userName(userRequest.getUserName())
                .email(userRequest.getEmail())
                .password(bCryptPasswordEncoder.encode(userRequest.getPassword()))
                .bio(userRequest.getBio())
                .build();

        String userId = userRepository.save(user).getId();
        userHashMap.put("userId", userId);

        return Pair.of(userHashMap, true);
    }

    @Override
    public Pair<HashMap<String, String>, Boolean> login(String userName, String password, HttpServletResponse response) {
        Query query = new Query();
        HashMap<String, String> userHashMap = new HashMap<>();
        query.addCriteria(Criteria.where("userName").is(userName));

        User user = mongoTemplate.findOne(query, User.class);

        if (user == null) {
            userHashMap.put("message", "username and/or password do not match");
            return Pair.of(userHashMap, false);
        }
        if (!passwordMatches(password, user.getPassword())) {
            userHashMap.put("message", "username and/or password do not match");
            return Pair.of(userHashMap, false);
        }

        setCookie(response, new HashMap<String, String>() {{
            put("userId", user.getId());
        }});

        userHashMap.put("message", "successfully logged in");
        return Pair.of(userHashMap, true);
    }

    @Override
    public HashMap<String, String> logout() {
        return null;
    }

    @Override
    public UserResponse updateUser(String userId, UserRequest userRequest) {

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(userId));
        User user = mongoTemplate.findOne(query, User.class);

        if (user != null) {
            user.setUserName(userRequest.getUserName());
            user.setEmail(userRequest.getEmail());
            user.setBio(userRequest.getBio());

            userRepository.save(user);

            return mapToUserResponse(user);
        }

        return null;
    }

    @Override
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserResponse getUserById(String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(userId));

        User user = mongoTemplate.findOne(query, User.class);

        if (user != null)
            return mapToUserResponse(user);
        return null;
    }

    @Override
    public UserResponse getUserByUserName(String userName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(userName));

        User user = mongoTemplate.findOne(query, User.class);

        if (user != null)
            return mapToUserResponse(user);
        return null;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::mapToUserResponse).toList();
    }

//    TODO: finish method
    private boolean userNameExists(String userName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(userName));
        return mongoTemplate.findOne(query, User.class) != null;
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

    private String readCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

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

    public Pair<UserWithPosts, Boolean> getUserPosts(String userId) {
        String microserviceUrl = "http://127.0.0.1:8081/api/post/" + userId + "/all";

        ResponseEntity<List<PostResponse>> responseEntity = restTemplate.exchange(
                microserviceUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PostResponse>>() {}
        );

        Query query = new Query();

        query.addCriteria(Criteria.where("id").is(userId));

        User user = mongoTemplate.findOne(query, User.class);

        if (!userExists(user)) {
            return Pair.of(null, false);
        }

//        TODO
//        ObjectNode jsonNodes = objectMapper.valueToTree(responseEntity.getBody());
//
//        jsonNodes.remove("userId");

        List<PostResponse> postResponseList = responseEntity.getBody();
        UserResponse userResponse = mapToUserResponse(user);

        UserWithPosts userWithPosts = new UserWithPosts(userResponse, postResponseList);

        return Pair.of(userWithPosts, true);
    }

    private boolean userExists(User user) {
        return user != null;
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
