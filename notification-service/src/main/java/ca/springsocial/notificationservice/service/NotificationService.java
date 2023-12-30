package ca.springsocial.notificationservice.service;

import ca.springsocial.notificationservice.dto.notification.NotificationResponse;
import ca.springsocial.notificationservice.events.friendship.FriendRequestSentEvent;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getUserNotifications(Long userId);
    List<NotificationResponse> getNotifications();
}
