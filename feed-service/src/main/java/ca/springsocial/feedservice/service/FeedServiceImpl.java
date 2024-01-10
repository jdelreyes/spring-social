package ca.springsocial.feedservice.service;

import ca.springsocial.feedservice.dto.feed.FeedResponse;
import ca.springsocial.feedservice.dto.friendship.FriendshipResponse;
import ca.springsocial.feedservice.dto.post.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Comparator;
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

        List<FeedResponse> feedResponseList = new ArrayList<>();

        friendshipResponseList.forEach(friendshipResponse -> {
            Long friendId = Objects.equals(friendshipResponse.getRecipientUserId(), userId) ?
                    friendshipResponse.getRequesterUserId() :
                    friendshipResponse.getRecipientUserId();

            List<PostResponse> postResponseList = webClient.get()
                    .uri(postServiceUri + "?userId=" + friendId)
                    .retrieve()
                    .bodyToFlux(PostResponse.class)
                    .collectList()
                    .block();

            assert postResponseList != null;
            postResponseList.forEach(postResponse -> {
                feedResponseList.add(this.mapPostResponseListToFeedResponseList(postResponse));
            });

        });
        feedResponseList.sort(Comparator.comparing(FeedResponse::getDateTimePosted));

        return feedResponseList;
    }

    @Override
    public List<FeedResponse> getFeed() {
        List<PostResponse> postResponseList = webClient.get()
                .uri(postServiceUri)
                .retrieve()
                .bodyToFlux(PostResponse.class)
                .collectList()
                .block();

        assert postResponseList != null;
        return postResponseList.stream().map(this::mapPostResponseListToFeedResponseList).toList();
    }

    private FeedResponse mapPostResponseListToFeedResponseList(PostResponse postResponse) {
        return new FeedResponse(postResponse.getId(),
                postResponse.getTitle(),
                postResponse.getContent(),
                postResponse.getDateTimePosted(),
                postResponse.getUserId());
    }
}
