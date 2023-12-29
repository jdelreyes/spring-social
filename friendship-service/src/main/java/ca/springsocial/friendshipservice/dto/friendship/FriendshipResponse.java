package ca.springsocial.friendshipservice.dto.friendship;

import ca.springsocial.friendshipservice.enums.FriendshipStatus;
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
