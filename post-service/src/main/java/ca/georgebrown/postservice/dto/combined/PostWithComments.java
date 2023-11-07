package ca.georgebrown.postservice.dto.combined;

import ca.georgebrown.postservice.dto.comment.CommentResponse;
import ca.georgebrown.postservice.dto.post.PostResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostWithComments {
    private String id;
    private String title;
    private String content;
    private LocalDateTime dateTimePosted;
    private Long userId;
    private List<CommentResponse> comments;

    public PostWithComments(PostResponse postResponse, List<CommentResponse> commentResponses) {
        this.id = postResponse.getId();
        this.title = postResponse.getTitle();
        this.content = postResponse.getContent();
        this.dateTimePosted = postResponse.getDateTimePosted();
        this.userId = postResponse.getUserId();
        this.comments = commentResponses;
    }
}
