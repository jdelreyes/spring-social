package ca.springsocial.commentservice.repository;

import ca.springsocial.commentservice.model.Comment;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    void deleteById(@Nonnull Long commentId);

    Comment getCommentById(Long commentId);

    List<Comment> getCommentsByUserId(Long userId);

    List<Comment> getCommentsByPostId(String postId);
}
