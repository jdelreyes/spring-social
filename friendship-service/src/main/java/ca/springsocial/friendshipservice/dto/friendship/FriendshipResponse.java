package ca.springsocial.friendshipservice.dto.friendship;

import ca.springsocial.friendshipservice.enums.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendshipResponse {
    private String id;
    private Long requesterUserId;
    private Long recipientUserId;
    private LocalDateTime dateTimeStatusChanged;
    private FriendshipStatus friendshipStatus;
}
