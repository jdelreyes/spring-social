package ca.springsocial.commentservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentCreatedEvent {
    private Long commentId;
    private Long authorId;
    private String postId;
}