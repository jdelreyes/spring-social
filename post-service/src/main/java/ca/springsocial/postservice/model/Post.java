package ca.springsocial.postservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(value = "posts")
public class Post {
    @Id
    private String id;
    private String title;
    private String content;
    @Builder.Default
    private LocalDateTime dateTimePosted = LocalDateTime.now();
    private Long userId;
}
