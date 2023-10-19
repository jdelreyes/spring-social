package ca.georgerbrown.friendshipservice.dto;

import ca.georgerbrown.friendshipservice.enums.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendshipRequest {
    private String requesterUserId;
    private String recipientUserId;
    private FriendshipStatus status;
}
