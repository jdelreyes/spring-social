package ca.springsocial.friendshipservice.bootstrap;

import ca.springsocial.friendshipservice.enums.FriendshipStatus;
import ca.springsocial.friendshipservice.model.Friendship;
import ca.springsocial.friendshipservice.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;


// stereotype
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final MongoTemplate mongoTemplate;
    private final FriendshipRepository friendshipRepository;

    //    runs the app when booting up
    @Override
    public void run(String... args) throws Exception {
//        we can seed the database while booting up
        if (friendshipRepository.findFriendshipById("657904a97da850294d2b9e7d") == null) {
            Friendship friendshipAccepted = Friendship.builder()
                    .id("657904a97da850294d2b9e7d")
                    .requesterUserId(1L)
                    .recipientUserId(2L)
                    .friendshipStatus(FriendshipStatus.accepted)
                    .build();

            friendshipRepository.save(friendshipAccepted);
        }

        if (friendshipRepository.findFriendshipById("6579047e7da850294d2b9e7c") == null) {
            Friendship friendshipPending = Friendship.builder()
                    .id("6579047e7da850294d2b9e7c")
                    .requesterUserId(2L)
                    .recipientUserId(3L)
                    .friendshipStatus(FriendshipStatus.pending)
                    .build();

            friendshipRepository.save(friendshipPending);
        }

        if (friendshipRepository.findFriendshipById("6579047e7da850294d2b9e7b") == null) {
            Friendship friendshipRejected = Friendship.builder()
                    .id("6579047e7da850294d2b9e7b")
                    .requesterUserId(3L)
                    .recipientUserId(1L)
                    .friendshipStatus(FriendshipStatus.rejected)
                    .build();

            friendshipRepository.save(friendshipRejected);
        }
    }
}
