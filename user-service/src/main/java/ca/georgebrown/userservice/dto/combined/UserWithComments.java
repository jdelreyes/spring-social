package ca.georgebrown.userservice.dto.combined;

import ca.georgebrown.userservice.dto.comment.CommentResponse;
import ca.georgebrown.userservice.dto.user.UserResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserWithComments {
    private String id;
    private String userName;
    private String email;
    private LocalDateTime dateTimeJoined;
    private String bio;
    private List<CommentResponse> comments;

    public UserWithComments(UserResponse userResponse, List<CommentResponse> commentResponses) {
        this.id = userResponse.getId();
        this.userName = userResponse.getUserName();
        this.email = userResponse.getEmail();
        this.dateTimeJoined = userResponse.getDateTimeJoined();
        this.bio = userResponse.getBio();
        this.comments = commentResponses;
    }
}
