package ca.springsocial.friendshipservice.dto.friendship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendshipRecipientRequest {
    private Long recipientUserId;
}
