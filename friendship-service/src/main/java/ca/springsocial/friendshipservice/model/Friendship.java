package ca.springsocial.friendshipservice.model;

import ca.springsocial.friendshipservice.enums.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

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
    @Builder.Default
    private LocalDateTime dateTimeStatusChanged = LocalDateTime.now();
    private FriendshipStatus friendshipStatus;
}
