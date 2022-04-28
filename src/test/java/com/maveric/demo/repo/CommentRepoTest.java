package com.maveric.demo.repo;


import com.maveric.demo.model.Comment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
@DataMongoTest
public class CommentRepoTest {

    @Autowired
    CommentRepo commentRepo;
    @BeforeEach
    void initUseCase() {
        Comment comment = createCommentList1();
        commentRepo.save(comment);
        Comment comment1 = createCommentList2();
        commentRepo.save(comment1);
    }



    @AfterEach
    void destroyByAll() {
        commentRepo.deleteAll();
    }


    @Test
     void findByPostId() {

        //List<Comment> comments= createCommentList();
        Optional<List<Comment>> comments = commentRepo.findByPostId("1");
        assertEquals(2, comments.get().size());
    }

    private Comment createCommentList1() {

        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setCommentId("1");
        comment.setComment("Hi");
        comment.setCreatedAt(null);
        comment.setCreatedAt(null);
        comment.setPostId("1");
        comment.setCommentedBy("123");
        comment.setLikesCount(2L);
        return comment;
    }
    private Comment createCommentList2() {
        Comment comment1 = new Comment();
        comment1.setCommentId("2");
        comment1.setComment("Hi");
        comment1.setCreatedAt(null);
        comment1.setCreatedAt(null);
        comment1.setPostId("1");
        comment1.setCommentedBy("123");
        comment1.setLikesCount(2L);
        return comment1;
    }
    @Test
    public void findByPostIdWithPage() {

        //List<Comment> comments= createCommentList();
        Optional<List<Comment>> comments = commentRepo.findByPostId("1", PageRequest.ofSize(2));
        assertEquals(2, comments.get().size());
    }

}

