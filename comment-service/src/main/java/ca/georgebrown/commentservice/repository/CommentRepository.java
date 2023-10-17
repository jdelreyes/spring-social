package ca.georgebrown.commentservice.repository;

import ca.georgebrown.commentservice.model.Comment;
import jakarta.annotation.Nonnull;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<Comment, String> {
    @DeleteQuery
    void deleteById(@Nonnull String commentId);
}
