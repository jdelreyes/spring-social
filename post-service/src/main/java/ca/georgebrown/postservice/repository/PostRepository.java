package ca.georgebrown.postservice.repository;

import ca.georgebrown.postservice.model.Post;
import jakarta.annotation.Nonnull;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post, String> {
    @DeleteQuery
    void deleteById(@Nonnull String postId);
}
