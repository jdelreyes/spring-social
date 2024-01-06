package ca.springsocial.notificationservice.events.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreatedEvent {
    private Long commentId;
    private Long authorId;
    private String postId;
}