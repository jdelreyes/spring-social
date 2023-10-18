package ca.georgebrown.userservice;

import ca.georgebrown.userservice.dto.user.UserRequest;
import ca.georgebrown.userservice.dto.user.UserResponse;
import ca.georgebrown.userservice.model.User;
import ca.georgebrown.userservice.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class UserServiceApplicationTests extends AbstractContainerBaseTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    MongoTemplate mongoTemplate;

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

        User user = User.builder()
                .id(uuid.toString())
				.userName("uniqueUserName2")
				.email("uniqueemail2@uemail.com")
				.password("uniquePassword2")
				.bio("uniqueBio2")
                .build();

        users.add(user);

        return users;
    }

	private String convertObjectToString(List<UserResponse> userResponseList) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(userResponseList);
	}

	private List<UserResponse> convertStringToObject(String jsonString) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(jsonString, new TypeReference<List<UserResponse>>() {});
	}

	@Test
	void createUser() throws Exception {
		UserRequest userRequest = getUserRequest();
		String userRequestJsonString = objectMapper.writeValueAsString(userRequest);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/user/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content(userRequestJsonString))
				.andExpect(MockMvcResultMatchers.status().isCreated());


//        Assertions
		Assertions.assertTrue(userRepository.findAll().size() == 1);

//        Query
		Query query = new Query();
		query.addCriteria(Criteria.where("userName").is("uniqueUserName"));
		List<User> userList = mongoTemplate.find(query, User.class);

		Assertions.assertTrue(userList.size() == 1);
	}

	@Test
	void loginUser() throws Exception {
		// Create a user
		UserRequest userRequest = getUserRequest();
		String userRequestJsonString = objectMapper.writeValueAsString(userRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/user/sign-up")
						.contentType(MediaType.APPLICATION_JSON)
						.content(userRequestJsonString))
				.andExpect(MockMvcResultMatchers.status().isCreated());

		// Attempt login with the created user
		mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(userRequestJsonString))
				.andExpect(MockMvcResultMatchers.status().isOk());

		// Try to log in with incorrect password
		userRequest.setPassword("wrongPassword");
		userRequestJsonString = objectMapper.writeValueAsString(userRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(userRequestJsonString))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	@Test
	void deleteUserTest() throws Exception {
		// Create a user
		UserRequest userRequest = getUserRequest();
		String userRequestJsonString = objectMapper.writeValueAsString(userRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/user/sign-up")
						.contentType(MediaType.APPLICATION_JSON)
						.content(userRequestJsonString))
				.andExpect(MockMvcResultMatchers.status().isCreated());

		// Retrieve user
		List<User> users = userRepository.findAll();
		User user = users.get(0);

		// Delete user
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/delete/" + user.getId()))
				.andExpect(MockMvcResultMatchers.status().isNoContent());

		// Ensure that the user is no longer present in the repository
		List<User> remainingUsers = userRepository.findAll();
		Assertions.assertTrue(remainingUsers.isEmpty());
	}
}
