package ca.georgebrown.userservice.controller;

import ca.georgebrown.userservice.dto.combined.UserWithComments;
import ca.georgebrown.userservice.dto.combined.UserWithPosts;
import ca.georgebrown.userservice.dto.combined.UserWithPostsWithComments;
import ca.georgebrown.userservice.dto.user.UserRequest;
import ca.georgebrown.userservice.dto.user.UserResponse;
import ca.georgebrown.userservice.service.UserServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    // CREATE
    @PostMapping({"/signup"})
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

    @GetMapping({"/{userNameOrId}/details"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponse> getUserByUserNameOrId(@PathVariable String userNameOrId) {
        UserResponse userResponse;
        userResponse = userService.getUserByUserName(userNameOrId);
        if (userResponse!=null)
            return new ResponseEntity<>(userResponse, HttpStatus.OK);

        userResponse = userService.getUserById(userNameOrId);
        if (userResponse!=null)
            return new ResponseEntity<>(userResponse, HttpStatus.OK);

        return new ResponseEntity<>(userResponse, HttpStatus.BAD_REQUEST);
    }

    @GetMapping({"/"})
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }


    // UPDATE
    @PutMapping({"/update/{userId}"})
    public ResponseEntity<?> updateUser(@PathVariable("userId") String userId, @RequestBody UserRequest userRequest) {
        boolean isUserUpdated = userService.updateUser(userId,userRequest);
        if (!isUserUpdated) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // DELETE
    @DeleteMapping({"/delete/{userId}"})
    public ResponseEntity<?> deleteUser(@PathVariable("userId") String userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping({"/{userId}/posts"})
    public ResponseEntity<UserWithPosts> getUserPosts(@PathVariable String userId) {
        UserWithPosts userWithPosts =  userService.getUserWithPosts(userId);

        if (userWithPosts == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userWithPosts, HttpStatus.OK);
    }

    @GetMapping({"/{userId}/comments"})
    public ResponseEntity<UserWithComments> getUserComments(@PathVariable String userId) {
        UserWithComments userWithComments =  userService.getUserWithComments(userId);
        if (userWithComments == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(userWithComments, HttpStatus.OK);
    }

    @GetMapping({"/{userId}/posts/comments"})
    public ResponseEntity<UserWithPostsWithComments> getUserWithPostsWithComments(@PathVariable String userId) {
        UserWithPostsWithComments userWithPostsWithComments = userService.getUserWithPostsWithComments(userId);
        if (userWithPostsWithComments == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(userWithPostsWithComments, HttpStatus.OK);
    }
}
