package ca.springsocial.commentservice.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "comments")
@Getter
@Setter
@Table
public class Comment {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String content;
    private LocalDateTime dateTimeCommented = LocalDateTime.now();
//    foreign keys
    private String postId;
    private Long userId;
}
