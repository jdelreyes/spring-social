package ca.springsocial.postservice;

import ca.springsocial.postservice.dto.post.PostRequest;
import ca.springsocial.postservice.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.CoreMatchers.containsString;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class PostServiceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PostService postService;


	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}
	@Test
	public void testCreatePost() throws Exception {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("title", "Test Title");
		requestBody.put("content", "Test Content");
		String jsonBody = objectMapper.writeValueAsString(requestBody);

		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addHeader("Content-Type", "application/json");

		mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/create")
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.requestAttr("httpServletRequest", mockRequest))
				.andExpect(MockMvcResultMatchers.status().isCreated());
	}


	@Test
	public void testGetUserPosts() throws Exception {
		Long userId = 123L;

		mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/user/{userId}", userId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				// Add more assertions based on the expected structure of the response
				.andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
	}




	@Test
	public void testUpdatePost() throws Exception {
		String postId = "123";
		PostRequest postRequest = new PostRequest();
		postRequest.setTitle("Updated Title");
		postRequest.setContent("Updated Content");

		// Configure the behavior of the mock
		when(postService.updatePost(eq(postId), any(PostRequest.class))).thenReturn(true);

		mockMvc.perform(put("/api/posts/update/{postId}", postId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(postRequest)))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Post with id " + postId + " successfully updated")));
	}

	@Test
	void testDeletePost() {
		// Given
		String postId = "1";

		// When
		postService.deletePost(postId);
	}

	private String asJsonString(Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
