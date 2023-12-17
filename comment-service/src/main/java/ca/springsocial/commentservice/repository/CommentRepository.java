package ca.springsocial.commentservice.repository;

import ca.springsocial.commentservice.model.Comment;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    void deleteById(@Nonnull Long commentId);

    Comment findCommentById(Long commentId);

    List<Comment> findCommentsByUserId(Long userId);

    List<Comment> findCommentsByPostId(String postId);

    List<Comment> findCommentsByUserIdAndPostId(Long userId, String postId);
}
