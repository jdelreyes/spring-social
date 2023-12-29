package ca.springsocial.notificationservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "notifications")
@Getter
@Setter
@Table
public class Notification {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dateTimeStamp = LocalDateTime.now();
}
