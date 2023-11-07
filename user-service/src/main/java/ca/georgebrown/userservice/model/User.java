package ca.georgebrown.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "users")
@Getter
@Setter
@Table
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
//    todo: make unique
    private String userName;
    private LocalDateTime dateTimeJoined = LocalDateTime.now();
    private String email;
    private String password;
    private String bio;
}
