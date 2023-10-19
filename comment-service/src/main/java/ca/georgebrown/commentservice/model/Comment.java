package ca.georgebrown.commentservice.model;

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
@Document(value = "comment")
public class Comment {
    @Id
    private String id;
    private String content;
    @Builder.Default
    private LocalDateTime dateTimeCommented = LocalDateTime.now();
//    foreign keys
    private String postId;
    private String userId;
}
