package ca.springsocial.notificationservice.repository;

import ca.springsocial.notificationservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findNotificationsByUserId(Long userId);
}
