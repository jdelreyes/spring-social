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
    //    repo
    private final MongoTemplate mongoTemplate;
    private final FriendshipRepository friendshipRepository;

    //    runs the app when booting up
    @Override
    public void run(String... args) throws Exception {
//        we can seed the database while booting up
        Friendship friendshipAccepted = Friendship.builder()
                .id("657904a97da850294d2b9e7d")
                .requesterUserId(1L)
                .recipientUserId(2L)
                .status(FriendshipStatus.accepted)
                .build();

        Friendship friendshipPending = Friendship.builder()
                .id("6579047e7da850294d2b9e7c")
                .requesterUserId(2L)
                .recipientUserId(3L)
                .status(FriendshipStatus.pending)
                .build();

        Friendship friendshipRejected = Friendship.builder()
                .id("6579047e7da850294d2b9e7b")
                .requesterUserId(3L)
                .recipientUserId(1L)
                .status(FriendshipStatus.rejected)
                .build();

        friendshipRepository.save(friendshipAccepted);
        friendshipRepository.save(friendshipPending);
        friendshipRepository.save(friendshipRejected);
    }
}
