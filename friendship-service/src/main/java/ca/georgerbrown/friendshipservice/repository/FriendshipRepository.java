package ca.georgerbrown.friendshipservice.repository;

import ca.georgerbrown.friendshipservice.enums.FriendshipStatus;
import ca.georgerbrown.friendshipservice.model.Friendship;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FriendshipRepository extends MongoRepository<Friendship, String> {
    List<Friendship> findByRecipientUserIdOrRequesterUserIdAndStatus(String recipientUserId, String requesterUserId, FriendshipStatus status);
}
