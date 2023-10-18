package ca.georgerbrown.friendshipservice.controller;

import ca.georgerbrown.friendshipservice.service.FriendshipServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/friendship")
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipServiceImpl friendshipService;
}
