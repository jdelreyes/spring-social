package ca.springsocial.feedservice.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class FeedResponse {
    public String id;
    public String title;
    public String content;
    public LocalDateTime dateTimePosted;
    public Long userId;
}
