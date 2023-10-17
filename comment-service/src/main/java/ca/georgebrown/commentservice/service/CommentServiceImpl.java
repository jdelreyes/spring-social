package ca.georgebrown.commentservice.service;

import ca.georgebrown.commentservice.dto.CommentRequest;
import ca.georgebrown.commentservice.dto.CommentResponse;
import ca.georgebrown.commentservice.model.Comment;
import ca.georgebrown.commentservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final MongoTemplate mongoTemplate;

//        TODO: to get userId, read from cookies
    @Override
    public HashMap<String, String> createComment(CommentRequest commentRequest) {

        Comment comment = Comment.builder()
                .content(commentRequest.getContent())
                .build();

        String commentId = commentRepository.save(comment).getId();

        return new HashMap<String, String>() {{
            put("commentId", commentId);
        }};
    }

    @Override
    public CommentResponse getComment(String commentId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(commentId));

        Comment comment = mongoTemplate.findOne(query, Comment.class);

        assert comment != null;
        return mapToCommentResponse(comment);
    }

    @Override
    public void updateComment(String commentId, CommentRequest commentRequest) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(commentId));

        Comment comment = mongoTemplate.findOne(query, Comment.class);

        if (comment != null) {
            comment.setContent(commentRequest.getContent());

            commentRepository.save(comment);
        }

    }

    @Override
    public void deleteComment(String commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentResponse> getAllCommentsFromUser(String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));

        List<Comment> commentList = mongoTemplate.find(query, Comment.class);

        return commentList.stream().map(this::mapToCommentResponse).toList();
    }

    @Override
    public List<CommentResponse> getAllComments() {
        return commentRepository.findAll().stream().map(this::mapToCommentResponse).toList();
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .dateTimeCommented(comment.getDateTimeCommented())
                .postId(comment.getPostId())
                .userId(comment.getUserId())
                .build();
    }
}
