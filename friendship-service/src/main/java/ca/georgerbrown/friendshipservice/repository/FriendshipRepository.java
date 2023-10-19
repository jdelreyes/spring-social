package ca.georgerbrown.friendshipservice.repository;

import ca.georgerbrown.friendshipservice.model.Friendship;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FriendshipRepository extends MongoRepository<Friendship, String> {
    @Query("{'recipientUserId': ?0, 'requesterUserId': ?1}")
    Friendship findByRecipientUserIdAndRequesterUserId(String recipientUserId, String requesterUserId);
}
