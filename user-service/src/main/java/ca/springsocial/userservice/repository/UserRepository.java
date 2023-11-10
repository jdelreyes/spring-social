package ca.springsocial.userservice.repository;

import ca.springsocial.userservice.model.User;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    void deleteById(@Nonnull Long userId);

    User getUserById(Long userId);

    User getUserByUserName(String userName);
}
