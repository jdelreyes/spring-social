package ca.georgebrown.userservice.repository;

import ca.georgebrown.userservice.model.User;
import jakarta.annotation.Nonnull;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    @DeleteQuery
    void deleteById(@Nonnull String userId);
}
