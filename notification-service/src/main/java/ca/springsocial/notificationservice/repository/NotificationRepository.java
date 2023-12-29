package ca.springsocial.notificationservice.repository;

import ca.springsocial.notificationservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
