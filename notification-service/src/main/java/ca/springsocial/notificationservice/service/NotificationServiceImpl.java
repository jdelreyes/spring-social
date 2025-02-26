package ca.springsocial.notificationservice.service;

import ca.springsocial.notificationservice.dto.friendship.FriendshipResponse;
import ca.springsocial.notificationservice.dto.notification.NotificationResponse;
import ca.springsocial.notificationservice.dto.post.PostResponse;
import ca.springsocial.notificationservice.dto.user.UserResponse;
import ca.springsocial.notificationservice.enums.friendship.FriendshipStatus;
import ca.springsocial.notificationservice.events.comment.CommentCreatedEvent;
import ca.springsocial.notificationservice.events.friendship.FriendRequestSentEvent;
import ca.springsocial.notificationservice.events.post.PostCreatedEvent;
import ca.springsocial.notificationservice.model.Notification;
import ca.springsocial.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final WebClient webClient;

    @Value("${user.service.url}")
    private String userServiceUri;
    @Value("${friendship.service.url}")
    private String friendshipServiceUri;
    @Value("${post.service.url}")
    private String postServiceUri;

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
        if (friendRequestSentEvent.getFriendshipStatus().equals(FriendshipStatus.pending)) {
            UserResponse userResponse = webClient.get()
                    .uri(userServiceUri + "/" + friendRequestSentEvent.getRequesterUserId())
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .onErrorResume(WebClientResponseException.class, ex -> ex.getStatusCode().is4xxClientError()
                            ? Mono.empty()
                            : Mono.error(ex))
                    .block();
            if (userResponse == null)
                return;

            Notification notification = new Notification();
            notification.setTitle("Friend Request Notification");
            notification.setDescription(userResponse.getUserName() + " has sent you a friend request");
            notification.setUserId(friendRequestSentEvent.getRecipientUserId());

            notificationRepository.save(notification);
        }
        if (friendRequestSentEvent.getFriendshipStatus().equals(FriendshipStatus.accepted)) {
            UserResponse userResponse = webClient.get()
                    .uri(userServiceUri + "/" + friendRequestSentEvent.getRecipientUserId())
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .onErrorResume(WebClientResponseException.class, ex -> ex.getStatusCode().is4xxClientError()
                            ? Mono.empty()
                            : Mono.error(ex))
                    .block();
            if (userResponse == null)
                return;

            Notification notification = new Notification();
            notification.setTitle("Friend Request Notification");
            notification.setDescription(userResponse.getUserName() + " has accepted your friend request");
            notification.setUserId(friendRequestSentEvent.getRequesterUserId());

            notificationRepository.save(notification);

        }
    }

    @KafkaListener(topics = "postCreatedEventTopic")
    private void handlePostCreatedNotification(PostCreatedEvent postCreatedEvent) {
        List<FriendshipResponse> friendshipResponseList = webClient.get()
                .uri(friendshipServiceUri + "/accepted-list?userId=" + postCreatedEvent.getAuthorId())
                .retrieve()
                .bodyToFlux(FriendshipResponse.class)
                .collectList()
                .block();

        if (friendshipResponseList == null) return;

        friendshipResponseList.forEach(friendshipResponse -> {
            Long friendId;
            Long authorId;
//            code might be confusing
//             see spring-social/friendship-service/src/main/java/ca/springsocial/friendshipservice/service/FriendshipServiceImpl.java
//             getUserFriendListByFriendshipStatus() method definition for more info
//             to summarize, Friendship model takes requester and recipient user id's to make a friendship object.
//             in order to get all the friends of a user, we have to query it so that it recognizes that a user can be
//             the requester or recipient
            if (Objects.equals(friendshipResponse.getRecipientUserId(), postCreatedEvent.getAuthorId())) {
                friendId = friendshipResponse.getRequesterUserId();
                authorId = friendshipResponse.getRecipientUserId();
            } else {
                friendId = friendshipResponse.getRecipientUserId();
                authorId = friendshipResponse.getRequesterUserId();
            }

            UserResponse userResponse = webClient.get()
                    .uri(userServiceUri + "/" + authorId)
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .block();

            if (userResponse == null) return;

            Notification notification = new Notification();
            notification.setTitle("Post Notification");
            notification.setDescription(userResponse.getUserName() + " has posted");
            notification.setUserId(friendId);

            notificationRepository.save(notification);
        });
    }

    @KafkaListener(topics = "commentCreatedEventTopic")
    private void handleCommentCreatedNotification(CommentCreatedEvent commentCreatedEvent) {
        PostResponse postResponse = webClient
                .get()
                .uri(postServiceUri + "/" + commentCreatedEvent.getPostId())
                .retrieve()
                .bodyToMono(PostResponse.class)
                .block();

        if (postResponse == null) return;
        Long postAuthorId = postResponse.getUserId();

        UserResponse userResponse = webClient
                .get()
                .uri(userServiceUri + "/" + commentCreatedEvent.getAuthorId())
                .retrieve()
                .bodyToMono(UserResponse.class)
                .block();

        if (userResponse == null) return;
        String commentAuthorUsername = userResponse.getUserName();

        Notification notification = new Notification();
        notification.setTitle("Comment Notification");
        notification.setDescription(commentAuthorUsername + " has commented to your post");
        notification.setUserId(postAuthorId);

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
