package ca.springsocial.notificationservice.service;

import ca.springsocial.notificationservice.events.friendship.FriendRequestSentEvent;
import ca.springsocial.notificationservice.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationServiceImpl implements  NotificationService{
    private final NotificationRepository notificationRepository;

    @Override
    @KafkaListener(topics = "notificationTopic")
    public void handleFriendRequestSentNotification(FriendRequestSentEvent friendRequestSentEvent) {
        log.info("friend request notification from {}", friendRequestSentEvent.getRequesterUserId());
    }
}
