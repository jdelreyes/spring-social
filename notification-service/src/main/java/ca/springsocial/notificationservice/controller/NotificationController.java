package ca.springsocial.notificationservice.controller;

import ca.springsocial.notificationservice.dto.notification.NotificationResponse;
import ca.springsocial.notificationservice.service.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationServiceImpl notificationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<NotificationResponse> getNotifications(@RequestParam(name = "userId") Optional<Long> userId) {
        if (userId.isPresent())
            return notificationService.getUserNotifications(userId.get());
        return notificationService.getNotifications();
    }
}
