package ca.springsocial.postservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCreatedEvent {
    private String id;
    private String userId;
    private LocalDateTime timestamp;
}
