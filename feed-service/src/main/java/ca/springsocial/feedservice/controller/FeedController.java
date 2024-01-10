package ca.springsocial.feedservice.controller;

import ca.springsocial.feedservice.dto.feed.FeedResponse;
import ca.springsocial.feedservice.service.FeedServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class FeedController {
    private final FeedServiceImpl feedService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<FeedResponse> getUserFeed(@RequestParam Optional<Long> userId) {
       if (userId.isEmpty())
           return feedService.getFeed();
       return feedService.getUserFeed(userId.get());
    }
}
