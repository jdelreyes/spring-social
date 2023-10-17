package ca.georgebrown.userservice.controller;

import ca.georgebrown.userservice.dto.combined.UserWithPosts;
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

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    // CREATE
    @PostMapping({"/signup"})
    public ResponseEntity<HashMap<String, String>> signUp(@RequestBody UserRequest userRequest){
        Pair<HashMap<String, String>, Boolean> userPair = userService.createUser(userRequest);

        if (userPair.getSecond())
            return new ResponseEntity<>(userPair.getFirst(), HttpStatus.CREATED);

        return new ResponseEntity<>(userPair.getFirst(), HttpStatus.CONFLICT);
    }

    @PostMapping({"/login"})
    public ResponseEntity<HashMap<String, String>> login(@RequestBody UserRequest userRequest, HttpServletResponse response) {
        Pair<HashMap<String, String>, Boolean> userPair = userService.login(userRequest.getUserName(), userRequest.getPassword(), response);

        if (userPair.getSecond())
            return new ResponseEntity<>(userPair.getFirst(), HttpStatus.OK);

        return new ResponseEntity<>(userPair.getFirst(), HttpStatus.BAD_REQUEST);
    }

//    @GetMapping({"/{userId}/details"})
//    @ResponseStatus(HttpStatus.OK)
//    public UserResponse getUserById(@PathVariable String userId) {
//        return userService.getUserById(userId);
//    }

    @GetMapping({"/{userName}/details"})
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserByUserName(@PathVariable String userName) {
        return userService.getUserByUserName(userName);
    }

    @GetMapping({"/all"})
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }


    // UPDATE
    @PutMapping({"/update/{userId}"})
    public ResponseEntity<UserResponse> updateUser(@PathVariable("userId") String userId, @RequestBody UserRequest userRequest) {
        UserResponse updatedUser = userService.updateUser(userId,userRequest);
        if (updatedUser == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(updatedUser, HttpStatus.NO_CONTENT);
    }

    // DELETE
    @DeleteMapping({"/delete/{userId}"})
    public ResponseEntity<?> deleteUser(@PathVariable("userId") String userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping({"/{userId}/posts"})
    public ResponseEntity<UserWithPosts> getUserPosts(@PathVariable String userId) {
        Pair<UserWithPosts, Boolean> result =  userService.getUserPosts(userId);
        boolean isSuccessful = result.getSecond();
        UserWithPosts userWithPosts = result.getFirst();

        if (!isSuccessful) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userWithPosts, HttpStatus.OK);
    }

}
