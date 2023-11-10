package ca.springsocial.friendshipservice.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


// stereotype
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
//    repo

    //    runs the app when booting up
    @Override
    public void run(String... args) throws Exception {
//        we can seed the database while booting up
    }
}
