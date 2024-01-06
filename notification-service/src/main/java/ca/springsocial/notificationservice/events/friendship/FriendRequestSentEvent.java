package ca.springsocial.notificationservice.events.friendship;

import ca.springsocial.notificationservice.enums.friendship.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestSentEvent {
    private String id;
    private Long recipientUserId;
    private Long requesterUserId;
    private FriendshipStatus friendshipStatus;
}
