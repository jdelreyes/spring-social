package ca.springsocial.friendshipservice.events;

import ca.springsocial.friendshipservice.enums.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestSentEvent {
    private Long recipientUserId;
    private Long requesterUserId;
    private FriendshipStatus friendshipStatus;
}
