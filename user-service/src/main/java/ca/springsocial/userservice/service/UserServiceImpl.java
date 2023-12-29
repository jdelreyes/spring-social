package ca.springsocial.userservice.service;

import ca.springsocial.userservice.dto.combined.UserWithComments;
import ca.springsocial.userservice.dto.combined.UserWithPosts;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<UserResponse> createUser(UserRequest userRequest) {
        if (!isEmailAddress(userRequest.getEmail()) && userRepository.findUserByUserName(userRequest.getUserName()) != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        user.setUserName(userRequest.getUserName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));
        user.setBio(userRequest.getBio());

        userRepository.save(user);
        return new ResponseEntity<>(mapToUserResponse(user), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<UserResponse> updateUser(Long userId, UserRequest userRequest) {
        User user = userRepository.findUserById(userId);

        if (user != null) {
            user.setUserName(userRequest.getUserName());
            user.setEmail(userRequest.getEmail());
            user.setBio(userRequest.getBio());

            userRepository.save(user);

            return new ResponseEntity<>(mapToUserResponse(user), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<UserWithPosts> getUserWithPosts(Long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        List<PostResponse> postResponseList = webClient
                .get()
                .uri(postServiceUri + "?userId=" + userId)
                .retrieve()
                .bodyToFlux(PostResponse.class)
                .collectList()
//                block to make this synchronous
                .block();

        UserResponse userResponse = mapToUserResponse(user);

        return new ResponseEntity<>(new UserWithPosts(userResponse, postResponseList), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserWithComments> getUserWithComments(Long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        List<CommentResponse> commentResponseList = webClient
                .get()
                .uri(commentServiceUri + "?userId=" + userId)
                .retrieve()
                .bodyToFlux(CommentResponse.class)
                .collectList()
//                block to make this synchronous
                .block();

        UserResponse userResponse = mapToUserResponse(user);

        return new ResponseEntity<>(new UserWithComments(userResponse, commentResponseList), HttpStatus.OK);
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
