package ca.springsocial.postservice;

import ca.springsocial.postservice.dto.post.PostRequest;
import ca.springsocial.postservice.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PostServiceApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PostRepository postRepository;
    private static Long userId = 1L;

//    @Test
//    @Order(1)
//    void createPost() throws Exception {
//        PostRequest postRequest = getPostRequest();
//        String postRequestJsonString = objectMapper.writeValueAsString(postRequest);
//
//
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(postRequestJsonString)
//                        .cookie("remember-me", ))
//                .andExpect(MockMvcResultMatchers.status().isCreated());
//
//        // assert
////        postRepository.findById()
//
//    }

    PostRequest getPostRequest() {
        return PostRequest.builder()
                .title("title")
                .content("content")
                .build();
    }
}
