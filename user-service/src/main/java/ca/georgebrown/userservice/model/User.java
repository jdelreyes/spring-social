package ca.georgebrown.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(value = "user")
public class User {
    @Id
    private String id;
//    unique
    private String userName;
    private LocalDateTime dateTimeJoined = LocalDateTime.now();
    private String email;
    private String password;
    private String bio;
}
