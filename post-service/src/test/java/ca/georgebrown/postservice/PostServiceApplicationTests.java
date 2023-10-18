package ca.georgebrown.postservice;

import ca.georgebrown.postservice.dto.post.PostRequest;
import ca.georgebrown.postservice.dto.post.PostResponse;
import ca.georgebrown.postservice.repository.PostRepository;
import ca.georgebrown.postservice.service.PostServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PostServiceApplicationTests {

	@Autowired
	private PostServiceImpl postService;

//	TODO:
//		NOTE: need new way of testing since parameter changed mb lol
	@Test
	void CreatePost() {
//		PostRequest postRequest = new PostRequest("Test Title", "Test Content");
//		String userId = "testUserId";
//
//		Map<String, Object> postIdMap = postService.createPost(postRequest, );
//
//		assertEquals(1, postService.getAllPosts().size()); // Assuming that getAllPosts() returns the list of all posts
//		assertTrue(postIdMap.containsKey("postId"));
	}

	@Test
	void getPostById() {

		PostResponse postResponse = postService.getPostById("testPostId");


		assertEquals("Expected Title", postResponse.getTitle());
		assertEquals("Expected Content", postResponse.getContent());
	}

	@Test
	void updatePost() {
		// Create a test post
//		PostRequest postRequest = new PostRequest("Test Title", "Test Content");
//		String userId = "testUserId";
//		HashMap<String, String> postIdMap = postService.createPost(userId, postRequest);
//
//		// Retrieve the post ID from the created post
//		String postId = postIdMap.get("postId");
//
//		// Update the post
//		PostRequest updatedPostRequest = new PostRequest("Updated Title", "Updated Content");
//		postService.updatePost(postId, updatedPostRequest);
//
//		// Retrieve the updated post
//		String updatedTitle = postService.getPostById(postId).getTitle();
//		String updatedContent = postService.getPostById(postId).getContent();
//
//		// Assert that the post has been updated
//		assertEquals("Updated Title", updatedTitle);
//		assertEquals("Updated Content", updatedContent);
	}
}