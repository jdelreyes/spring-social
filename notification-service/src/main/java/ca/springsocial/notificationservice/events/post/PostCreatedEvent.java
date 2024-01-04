package ca.springsocial.notificationservice.events.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCreatedEvent {
    private Long userId;
    private String postId;
}
