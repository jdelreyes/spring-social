package ca.springsocial.userservice.bootstrap;

import ca.springsocial.userservice.model.User;
import ca.springsocial.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// stereotype
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //    runs the app when booting up
    @Override
    public void run(String... args) throws Exception {
//        we can seed the database while booting up
        if (userRepository.findUserByUserName("John Doe") == null) {
            User widgets = new User();
            widgets.setUserName("John Doe");
            widgets.setEmail("johndoe@email.com");
            widgets.setPassword(bCryptPasswordEncoder.encode("password"));
            widgets.setBio("I love dogs <3");
            widgets.setDateTimeJoined(LocalDateTime.now());
            userRepository.save(widgets);
        }

        if (userRepository.findUserByUserName("Jane Smith") == null) {
            User widgets = new User();
            widgets.setUserName("Jane Smith");
            widgets.setEmail("janesmith@email.com");
            widgets.setPassword(bCryptPasswordEncoder.encode("password"));
            widgets.setBio("Nature enthusiast and book lover");
            widgets.setDateTimeJoined(LocalDateTime.now());
            userRepository.save(widgets);
        }

        if (userRepository.findUserByUserName("Bob Johnson") == null) {
            User widgets = new User();
            widgets.setUserName("Bob Johnson");
            widgets.setEmail("bobjohnson@email.com");
            widgets.setPassword(bCryptPasswordEncoder.encode("password"));
            widgets.setBio("Passionate about technology and coding");
            widgets.setDateTimeJoined(LocalDateTime.now());
            userRepository.save(widgets);
        }
    }
}
