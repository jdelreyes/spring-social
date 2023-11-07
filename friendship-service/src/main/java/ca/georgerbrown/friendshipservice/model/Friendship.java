package ca.georgerbrown.friendshipservice.model;

import ca.georgerbrown.friendshipservice.enums.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(value = "friendships")
public class Friendship {
    @Id
    private String id;
    private Long requesterUserId;
    private Long recipientUserId;
    private FriendshipStatus status;
}
