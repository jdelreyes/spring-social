package ca.springsocial.userservice;

import ca.springsocial.userservice.dto.post.PostResponse;
import ca.springsocial.userservice.dto.user.UserRequest;
import ca.springsocial.userservice.dto.user.UserResponse;
import ca.springsocial.userservice.model.User;
import ca.springsocial.userservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceApplicationTests extends AbstractContainerBaseTest {
    private static MockWebServer mockWebServer;
    private static Long userId;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    @Test
    @Order(1)
    void signUp() throws Exception {
        UserRequest userRequest = getUserRequest();
        String userRequestJsonString = objectMapper.writeValueAsString(userRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRequestJsonString))
                .andExpect(MockMvcResultMatchers.status().isCreated());


        // assertions
        assertEquals(1, userRepository.findAll().size());

        // then
        User user = userRepository.findUserByUserName("uniqueUserName");
        assertEquals(userRequest.getUserName(), user.getUserName());

        userId = user.getId();
    }

    @Test
    @Order(2)
    void getUserById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/" + userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // assert
                .andExpect(content()
                        .string(objectMapper
                                .writeValueAsString(mapToUserResponse(userRepository
                                        .findUserById(userId)))));
    }

    @Test
    @Order(3)
    void updateUser() throws Exception {
        UserRequest updatedUserRequest = getUpdatedUserRequest();
        String updatedUserRequestJsonString = objectMapper.writeValueAsString(updatedUserRequest);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedUserRequestJsonString))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // assert
        User updatedUser = userRepository.findUserByUserName(updatedUserRequest.getUserName());
        assertEquals(updatedUserRequest.getUserName(), updatedUser.getUserName());

    }

    @Test
    @Order(4)
    void login() throws Exception {
        UserRequest userLoginRequest = getUserLoginRequest();
        String userLoginRequestJsonString = objectMapper.writeValueAsString(userLoginRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userLoginRequestJsonString))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // assert
                .andExpect(cookie().exists("remember-me")); // cookie name is remember-me
    }

    @Test
    @Order(5)
    void getUserWithPosts() throws Exception {
        List<PostResponse> postResponseList = getPostResponseList();

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(List.of(postResponseList)))
                .addHeader("Content-Type", "application/json"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/" + userId))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    // todo
    @Test
    @Order(6)
    void deleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/" + userId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        //assert
        User deletedUser = userRepository.findUserById(userId);
        assertEquals(deletedUser, null);
    }

    private List<PostResponse> getPostResponseList() {
        List<PostResponse> postResponseList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            PostResponse postResponse = PostResponse.builder()
                    .id("postId" + i)
                    .title("title" + i)
                    .content("content" + i)
                    .dateTimePosted(LocalDateTime.now())
                    .userId(userId)
                    .build();
            postResponseList.add(postResponse);
        }

        return postResponseList;
    }

    private UserRequest getUserRequest() {
        return UserRequest.builder()
                .userName("uniqueUserName")
                .email("user@domain.ca")
                .password("password")
                .bio("bio")
                .build();
    }

    private UserRequest getUserLoginRequest() {
        return UserRequest.builder()
                .userName("updatedUserName")
                .password("password")
                .build();
    }

    private UserRequest getUpdatedUserRequest() {
        return UserRequest.builder()
                .userName("updatedUserName")
                .email("updatedUser@doman.ca")
                .build();
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
