package ca.springsocial.feedservice.dto.friendship;

import ca.springsocial.feedservice.enums.friendship.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendshipResponse {
    private String id;
    private Long requesterUserId;
    private Long recipientUserId;
    private FriendshipStatus friendshipStatus;
}
