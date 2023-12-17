package ca.springsocial.userservice.bootstrap;

import ca.springsocial.userservice.model.User;
import ca.springsocial.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// stereotype
@Component
@RequiredArgsConstructor
// allows us to disable bootstrap when the web app is starting
@Profile("!test")
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //    runs the app when booting up
    @Override
    public void run(String... args) throws Exception {
//        we can seed the database while booting up
        if (userRepository.findUserByUserName("John Doe") == null) {
            User widget = new User();
            widget.setId(1L);
            widget.setUserName("John Doe");
            widget.setEmail("johndoe@email.com");
            widget.setPassword(bCryptPasswordEncoder.encode("password"));
            widget.setBio("I love dogs <3");
            widget.setDateTimeJoined(LocalDateTime.now());
            userRepository.save(widget);
        }

        if (userRepository.findUserByUserName("Jane Smith") == null) {
            User widget = new User();
            widget.setId(2L);
            widget.setUserName("Jane Smith");
            widget.setEmail("janesmith@email.com");
            widget.setPassword(bCryptPasswordEncoder.encode("password"));
            widget.setBio("Nature enthusiast and book lover");
            widget.setDateTimeJoined(LocalDateTime.now());
            userRepository.save(widget);
        }

        if (userRepository.findUserByUserName("Bob Johnson") == null) {
            User widget = new User();
            widget.setId(3L);
            widget.setUserName("Bob Johnson");
            widget.setEmail("bobjohnson@email.com");
            widget.setPassword(bCryptPasswordEncoder.encode("password"));
            widget.setBio("Passionate about technology and coding");
            widget.setDateTimeJoined(LocalDateTime.now());
            userRepository.save(widget);
        }
    }
}
