package ca.georgerbrown.friendshipservice.repository;

import ca.georgerbrown.friendshipservice.enums.FriendshipStatus;
import ca.georgerbrown.friendshipservice.model.Friendship;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FriendshipRepository extends MongoRepository<Friendship, Long> {
    @Query("{'requesterUserId':  ?0, 'recipientUserId': ?1}")
    Friendship findByRequesterUserIdAndRecipientUserId(Long requesterUserId,Long recipientUserId);

    Friendship findById(String id);

    List<Friendship> findAllByRecipientUserIdOrRequesterUserIdAndStatus(Long recipientUserId, Long requesterUserId, FriendshipStatus friendshipStatus);
}
