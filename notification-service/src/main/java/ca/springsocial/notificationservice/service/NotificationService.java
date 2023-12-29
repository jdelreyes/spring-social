package ca.springsocial.notificationservice.service;

import ca.springsocial.notificationservice.events.friendship.FriendRequestSentEvent;

public interface NotificationService {
    void handleFriendRequestSentNotification(FriendRequestSentEvent friendRequestSentEvent);
}
