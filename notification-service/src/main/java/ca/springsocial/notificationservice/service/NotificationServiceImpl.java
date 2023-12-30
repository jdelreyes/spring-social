package ca.springsocial.notificationservice.service;

import ca.springsocial.notificationservice.dto.notification.NotificationResponse;
import ca.springsocial.notificationservice.dto.user.UserResponse;
import ca.springsocial.notificationservice.events.friendship.FriendRequestSentEvent;
import ca.springsocial.notificationservice.model.Notification;
import ca.springsocial.notificationservice.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final WebClient webClient;

    @Value("${user.service.url}")
    private String userServiceUri;

    @Override
    public List<NotificationResponse> getUserNotifications(Long userId) {
        List<Notification> notificationList = notificationRepository.findNotificationsByUserId(userId);
        return notificationList.stream().map(this::mapToNotificationResponse).toList();
    }

    @Override
    public List<NotificationResponse> getNotifications() {
        List<Notification> notificationList = notificationRepository.findAll();
        return notificationList.stream().map(this::mapToNotificationResponse).toList();
    }

    @KafkaListener(topics = "friendRequestSentEventTopic")
    private void handleFriendRequestSentNotification(FriendRequestSentEvent friendRequestSentEvent) {
        log.info("friend request object: " + friendRequestSentEvent);
//        fixme: Caused by: java.lang.NullPointerException:
//         Cannot invoke "org.springframework.web.reactive.function.client.WebClient.get()" because "this.webClient" is null
//         I used @RequiredArgsConstructor to inject final fields including WebClient but it still doesnt work
//         ive already checked dependencies and there was a webflux dependency included already
        UserResponse userResponse = webClient.get()
                .uri(userServiceUri + "/" + friendRequestSentEvent.getRequesterUserId())
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, ex -> ex.getStatusCode().is4xxClientError()
                        ? Mono.empty()
                        : Mono.error(ex))
                .block();

        log.info("userResponse: " + userResponse);
        log.info(" " + userResponse.getUserName());
        log.info(" " + friendRequestSentEvent.getRequesterUserId() + " " + friendRequestSentEvent.getRecipientUserId());

        Notification notification = new Notification();
        notification.setTitle("Friend Request Notification");
        notification.setDescription(userResponse.getUserName() + " has sent you a friend request");
        notification.setUserId(friendRequestSentEvent.getRecipientUserId());

        notificationRepository.save(notification);
    }

    private NotificationResponse mapToNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .description(notification.getDescription())
                .localDateTimeStamp(notification.getLocalDateTimeStamp())
                .userId(notification.getUserId())
                .build();
    }
}
