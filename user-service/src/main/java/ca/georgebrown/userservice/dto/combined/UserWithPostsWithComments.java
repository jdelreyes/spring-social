package ca.georgebrown.userservice.dto.combined;

import ca.georgebrown.userservice.dto.user.UserResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserWithPostsWithComments {
    private String id;
    private String userName;
    private String email;
    private LocalDateTime dateTimeJoined;
    private String bio;
    private List<PostWithComments> postWithComments;

    public UserWithPostsWithComments(UserResponse userResponse, List<PostWithComments> postWithComments) {
        this.id = userResponse.getId();
        this.userName = userResponse.getUserName();
        this.email = userResponse.getEmail();
        this.dateTimeJoined = userResponse.getDateTimeJoined();
        this.bio = userResponse.getBio();
        this.postWithComments = postWithComments;
    }
}
