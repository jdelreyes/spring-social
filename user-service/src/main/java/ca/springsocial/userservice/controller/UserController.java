package ca.springsocial.userservice.controller;

import ca.springsocial.userservice.dto.combined.UserWithComments;
import ca.springsocial.userservice.dto.combined.UserWithPosts;
import ca.springsocial.userservice.dto.combined.UserWithPostsWithComments;
import ca.springsocial.userservice.dto.user.UserRequest;
import ca.springsocial.userservice.dto.user.UserResponse;
import ca.springsocial.userservice.service.UserServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    // CREATE
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody UserRequest userRequest){
        Map<String, Object> userHashMap = userService.signUp(userRequest);

        if ((Boolean) userHashMap.get("status"))
            return new ResponseEntity<>(userHashMap, HttpStatus.CREATED);

        return new ResponseEntity<>(userHashMap, HttpStatus.CONFLICT);
    }

    @PostMapping({"/login"})
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserRequest userRequest, HttpServletResponse response) {
        Map<String, Object> userHashMap = userService.login(userRequest.getUserName(), userRequest.getPassword(), response);

        if ((Boolean) userHashMap.get("status"))
            return new ResponseEntity<>(userHashMap, HttpStatus.OK);

        return new ResponseEntity<>(userHashMap, HttpStatus.BAD_REQUEST);
    }

    @PostMapping({"/logout"})
    @ResponseStatus(HttpStatus.OK)
    Map<String, Object> logout(HttpServletResponse httpServletResponse){
        return userService.logout(httpServletResponse);
    }

    @GetMapping({"/{userId}/details"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        UserResponse userResponse = userService.getUserById(userId);
        if (userResponse!=null)
            return new ResponseEntity<>(userResponse, HttpStatus.OK);

        return new ResponseEntity<>(userResponse, HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getUsers() {
        return userService.getUsers();
    }


    // UPDATE
    @PutMapping({"/update/{userId}"})
    public ResponseEntity<?> updateUser(@PathVariable("userId") Long userId, @RequestBody UserRequest userRequest) {
        boolean isUserUpdated = userService.updateUser(userId,userRequest);
        if (!isUserUpdated) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // DELETE
    @DeleteMapping({"/delete/{userId}"})
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping({"/{userId}/posts"})
    public ResponseEntity<UserWithPosts> getUserPosts(@PathVariable Long userId) {
        UserWithPosts userWithPosts =  userService.getUserWithPosts(userId);

        if (userWithPosts == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userWithPosts, HttpStatus.OK);
    }

    @GetMapping({"/{userId}/comments"})
    public ResponseEntity<UserWithComments> getUserComments(@PathVariable Long userId) {
        UserWithComments userWithComments =  userService.getUserWithComments(userId);
        if (userWithComments == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(userWithComments, HttpStatus.OK);
    }

    @GetMapping({"/{userId}/posts/comments"})
    public ResponseEntity<UserWithPostsWithComments> getUserWithPostsWithComments(@PathVariable Long userId) {
        UserWithPostsWithComments userWithPostsWithComments = userService.getUserWithPostsWithComments(userId);
        if (userWithPostsWithComments == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(userWithPostsWithComments, HttpStatus.OK);
    }
}
