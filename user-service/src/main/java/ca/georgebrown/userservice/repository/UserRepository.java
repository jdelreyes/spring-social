package ca.georgebrown.userservice.repository;

import ca.georgebrown.userservice.model.User;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>{
    void deleteById(@Nonnull Long userId);
    User getUserById(Long userId);
    User getUserByUserName(String userName);
}
