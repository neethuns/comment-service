package com.maveric.demo.repo;

import com.maveric.demo.model.Comment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepo extends MongoRepository<Comment, String> {

    Optional<List<Comment>> findByPostId(String postId);
    Optional<List<Comment>> findByPostId(String postId, PageRequest page);
    Optional<Comment> findByCommentId(String commentId);
}
