package ca.springsocial.friendshipservice.repository;

import ca.springsocial.friendshipservice.enums.FriendshipStatus;
import ca.springsocial.friendshipservice.model.Friendship;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FriendshipRepository extends MongoRepository<Friendship, String> {
    Friendship findFriendshipByRecipientUserIdAndRequesterUserId(Long recipientUserId, Long requesterId);

    Friendship findFriendshipById(String friendshipId);

    List<Friendship> findFriendshipByRecipientUserIdOrRequesterUserIdAndFriendshipStatus(Long recipientUserId, Long requesterUserId, FriendshipStatus friendshipStatus);

    List<Friendship> findFriendshipByFriendshipStatus(FriendshipStatus friendshipStatus);

}
