package ca.springsocial.feedservice.service;

import ca.springsocial.feedservice.dto.feed.FeedResponse;

import java.util.List;

public interface FeedService {
    List<FeedResponse> getUserFeed(Long userId);

    List<FeedResponse> getFeed();
}
