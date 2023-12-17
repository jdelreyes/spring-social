package ca.springsocial.commentservice.bootstrap;

import ca.springsocial.commentservice.model.Comment;
import ca.springsocial.commentservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


// stereotype
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    //    repo
    private final CommentRepository commentRepository;

    //    runs the app when booting up
    @Override
    public void run(String... args) throws Exception {
//        we can seed the database while booting up
        if (commentRepository.findCommentById(1L) == null) {
            Comment widget = new Comment();
            widget.setContent("Great content!");
            widget.setDateTimeCommented(LocalDateTime.now());
            widget.setUserId(1L);
            widget.setPostId("654d41fb68e15135137d7a75");
            commentRepository.save(widget);
        }

        if (commentRepository.findCommentById(2L) == null) {
            Comment widget = new Comment();
            widget.setContent("Interesting read!");
            widget.setDateTimeCommented(LocalDateTime.now());
            widget.setUserId(2L);
            widget.setPostId("654d41f968e15135137d7a74");
            commentRepository.save(widget);
        }

        if (commentRepository.findCommentById(3L) == null) {
            Comment widget = new Comment();
            widget.setContent("Thanks for sharing!");
            widget.setDateTimeCommented(LocalDateTime.now());
            widget.setUserId(3L);
            widget.setPostId("654d759f35b2e84723069384");
            commentRepository.save(widget);
        }

        if (commentRepository.findCommentById(4L) == null) {
            Comment widget = new Comment();
            widget.setContent("Well written!");
            widget.setDateTimeCommented(LocalDateTime.now());
            widget.setUserId(1L);
            widget.setPostId("6548359f35b2e07489283153");
            commentRepository.save(widget);
        }

        if (commentRepository.findCommentById(5L) == null) {
            Comment widget = new Comment();
            widget.setContent("Enjoyed reading this!");
            widget.setDateTimeCommented(LocalDateTime.now());
            widget.setUserId(2L);
            widget.setPostId("654d359f35b2e07481048375");
            commentRepository.save(widget);
        }

        if (commentRepository.findCommentById(6L) == null) {
            Comment widget = new Comment();
            widget.setContent("Informative post!");
            widget.setDateTimeCommented(LocalDateTime.now());
            widget.setUserId(3L);
            widget.setPostId("654d759f35b2e07481068943");
            commentRepository.save(widget);
        }

        if (commentRepository.findCommentById(7L) == null) {
            Comment widget = new Comment();
            widget.setContent("Couldn't agree more!");
            widget.setDateTimeCommented(LocalDateTime.now());
            widget.setUserId(1L);
            widget.setPostId("654d41fb68e15135137d7a75");
            commentRepository.save(widget);
        }

        if (commentRepository.findCommentById(8L) == null) {
            Comment widget = new Comment();
            widget.setContent("Well said!");
            widget.setDateTimeCommented(LocalDateTime.now());
            widget.setUserId(2L);
            widget.setPostId("654d41f968e15135137d7a74");
            commentRepository.save(widget);
        }

        if (commentRepository.findCommentById(9L) == null) {
            Comment widget = new Comment();
            widget.setContent("Fantastic post!");
            widget.setDateTimeCommented(LocalDateTime.now());
            widget.setUserId(3L);
            widget.setPostId("654d759f35b2e84723069384");
            commentRepository.save(widget);
        }

        if (commentRepository.findCommentById(10L) == null) {
            Comment widget = new Comment();
            widget.setContent("Insightful!");
            widget.setDateTimeCommented(LocalDateTime.now());
            widget.setUserId(1L);
            widget.setPostId("6548359f35b2e07489283153");
            commentRepository.save(widget);
        }

        if (commentRepository.findCommentById(11L) == null) {
            Comment widget = new Comment();
            widget.setContent("Thanks for sharing your thoughts!");
            widget.setDateTimeCommented(LocalDateTime.now());
            widget.setUserId(2L);
            widget.setPostId("654d359f35b2e07481048375");
            commentRepository.save(widget);
        }

        if (commentRepository.findCommentById(12L) == null) {
            Comment widget = new Comment();
            widget.setContent("Interesting perspective!");
            widget.setDateTimeCommented(LocalDateTime.now());
            widget.setUserId(3L);
            widget.setPostId("654d759f35b2e07481068943");
            commentRepository.save(widget);
        }

        if (commentRepository.findCommentById(13L) == null) {
            Comment widget = new Comment();
            widget.setContent("Appreciate your insights!");
            widget.setDateTimeCommented(LocalDateTime.now());
            widget.setUserId(1L);
            widget.setPostId("654d41fb68e15135137d7a75");
            commentRepository.save(widget);
        }

        if (commentRepository.findCommentById(14L) == null) {
            Comment widget = new Comment();
            widget.setContent("Well articulated!");
            widget.setDateTimeCommented(LocalDateTime.now());
            widget.setUserId(2L);
            widget.setPostId("654d41f968e15135137d7a74");
            commentRepository.save(widget);
        }

        if (commentRepository.findCommentById(15L) == null) {
            Comment widget = new Comment();
            widget.setContent("I wish I had your brain!");
            widget.setDateTimeCommented(LocalDateTime.now());
            widget.setUserId(2L);
            widget.setPostId("654d359f35b2e07481048375");
            commentRepository.save(widget);
        }

    }
}
