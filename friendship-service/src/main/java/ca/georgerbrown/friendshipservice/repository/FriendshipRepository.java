package ca.georgerbrown.friendshipservice.repository;

import ca.georgerbrown.friendshipservice.model.Friendship;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FriendshipRepository extends MongoRepository<Friendship, String> {
    List<Friendship> findBySenderIdOrReceiverIdAndStatus(String userId1, String userId2, String status);
}
