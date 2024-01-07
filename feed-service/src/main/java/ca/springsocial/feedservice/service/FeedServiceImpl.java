package ca.springsocial.feedservice.service;

import ca.springsocial.feedservice.dto.feed.FeedResponse;
import ca.springsocial.feedservice.dto.friendship.FriendshipResponse;
import ca.springsocial.feedservice.dto.post.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedServiceImpl implements FeedService {
    private final WebClient webClient;

    @Value("${post.service.url}")
    private String postServiceUri;
    @Value("${friendship.service.url}")
    private String friendshipServiceUri;

    @Override
    public List<FeedResponse> getUserFeed(Long userId) {
//        ask friendship service for user's friend
        List<FriendshipResponse> friendshipResponseList = webClient.get()
                .uri(friendshipServiceUri + "/accepted-list?userId=" + userId)
                .retrieve()
                .bodyToFlux(FriendshipResponse.class)
                .collectList()
                .block();

        if (friendshipResponseList == null) return null;

        friendshipResponseList.forEach(friendshipResponse -> {
            Long friendId;
            Long authorId;
//            code might be confusing
//             see spring-social/friendship-service/src/main/java/ca/springsocial/friendshipservice/service/FriendshipServiceImpl.java
//             getUserFriendListByFriendshipStatus() method definition for more info
//             to summarize, Friendship model takes requester and recipient user id's to make a friendship object.
//             in order to get all the friends of a user, we have to query it so that it recognizes that a user can be
//             the requester or recipient
            if (Objects.equals(friendshipResponse.getRecipientUserId(), userId)) {
                friendId = friendshipResponse.getRequesterUserId();
                authorId = friendshipResponse.getRecipientUserId();
            } else {
                friendId = friendshipResponse.getRecipientUserId();
                authorId = friendshipResponse.getRequesterUserId();
            }


        });

        return null;
    }

    @Override
    public List<FeedResponse> getFeed() {
        return null;
    }

    private List<FeedResponse> mapPostResponseListToFeedResponseList(List<PostResponse> postResponseList) {
//        postResponseList.stream().
                return null;
    }
}
