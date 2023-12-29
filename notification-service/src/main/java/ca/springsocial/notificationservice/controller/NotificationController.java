package ca.springsocial.notificationservice.controller;

import ca.springsocial.notificationservice.dto.NotificationResponse;
import ca.springsocial.notificationservice.service.NotificationServiceImpl;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationServiceImpl notificationService;

    @GetMapping
    public ResponseEntity<NotificationResponse> getUserNotification() {
//        todo:
       return null;
    }
}
