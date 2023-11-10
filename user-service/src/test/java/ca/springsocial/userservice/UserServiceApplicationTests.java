package ca.springsocial.userservice;

import ca.springsocial.userservice.dto.user.UserRequest;
import ca.springsocial.userservice.dto.user.UserResponse;
import ca.springsocial.userservice.model.User;
import ca.springsocial.userservice.repository.UserRepository;
import ca.springsocial.userservice.service.UserServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class UserServiceApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Mock
    private UserServiceImpl userService;
    @Autowired
    private UserRepository userRepository;

    public UserRequest getUserRequest() {
        return UserRequest.builder()
                .userName("uniqueUserName")
                .email("uniqueemail@uemail.com")
                .password("uniquePassword")
                .bio("uniqueBio")
                .build();
    }

    private List<User> getUserList() {
        List<User> users = new ArrayList<>();
        UUID uuid = UUID.randomUUID();

        User user = new User();
        user.setId(Long.parseLong(uuid.toString()));
        user.setUserName("uniqueUserName2");
        user.setEmail("uniqueemail2@uemail.com");
        user.setPassword("uniquePassword2");
        user.setBio("uniqueBio2");

        users.add(user);

        return users;
    }

    private String convertObjectToString(List<UserResponse> userResponseList) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(userResponseList);
    }

    private List<UserResponse> convertStringToObject(String jsonString) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, new TypeReference<List<UserResponse>>() {
        });
    }

    @Test
    void signUpTest() throws Exception {
        // Mocking the service method
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("status", true);
        when(userService.signUp(any(UserRequest.class))).thenReturn(mockResponse);

        // Creating a sample userRequest
        UserRequest userRequest = new UserRequest();
        userRequest.setUserName("testUser");
        userRequest.setEmail("test@test.com");
        userRequest.setPassword("password123");

        // Converting the userRequest to JSON
        String userRequestJsonString = objectMapper.writeValueAsString(userRequest);

        // Making the POST request and validating the response
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRequestJsonString))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void loginUser() throws Exception {
        // Create a user
        UserRequest userRequest = getUserRequest();
        String userRequestJsonString = objectMapper.writeValueAsString(userRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRequestJsonString))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Attempt login with the created user
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRequestJsonString))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Try to log in with incorrect password
        userRequest.setPassword("wrongPassword");
        userRequestJsonString = objectMapper.writeValueAsString(userRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRequestJsonString))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deleteUserTest() throws Exception {
        // Create a user
        UserRequest userRequest = getUserRequest();
        String userRequestJsonString = objectMapper.writeValueAsString(userRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRequestJsonString))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Retrieve user
        List<User> users = userRepository.findAll();
        User user = users.get(0);

        // Delete user
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/delete/" + user.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // Ensure that the user is no longer present in the repository
        List<User> remainingUsers = userRepository.findAll();
        Assertions.assertTrue(remainingUsers.isEmpty());
    }

    @Test
    void updateUserTest() throws Exception {
        // Create a user
        UserRequest userRequest = getUserRequest();
        String userRequestJsonString = objectMapper.writeValueAsString(userRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRequestJsonString))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Retrieve user
        List<User> users = userRepository.findAll();
        User user = users.get(0);

        // Update the user
        userRequest.setUserName("updatedUserName");
        userRequestJsonString = objectMapper.writeValueAsString(userRequest);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/update/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRequestJsonString))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // Verify that the user has been updated
        User updatedUser = userRepository.findById(user.getId()).orElse(null);
        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals("updatedUserName", updatedUser.getUserName());
    }

}
