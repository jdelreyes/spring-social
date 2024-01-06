package ca.springsocial.notificationservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class PostResponse {
    public String id;
    public String title;
    public String content;
    public LocalDateTime dateTimePosted;
    public Long userId;
}
