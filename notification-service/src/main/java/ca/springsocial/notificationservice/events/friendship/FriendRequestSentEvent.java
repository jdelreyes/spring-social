package ca.springsocial.notificationservice.events.friendship;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestSentEvent {
    private Long recipientUserId;
    private Long requesterUserId;
}
