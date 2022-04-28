package com.maveric.demo.controller;



import com.fasterxml.jackson.databind.ObjectMapper;

import com.maveric.demo.constant.CommentConstant;
import com.maveric.demo.dto.CommentDto;
import com.maveric.demo.dto.UserDto;
import com.maveric.demo.model.Comment;
import com.maveric.demo.service.CommentService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WebMvcTest(CommentController.class)
public class CommentControllerTest {
    @MockBean
    CommentService commentService;

    @Autowired
    MockMvc mockMvc;

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetComments()throws Exception {
        List<CommentDto> commentDto = createCommentList();

        Mockito.when(commentService.getAllComments("2", null, null)).thenReturn(commentDto);

        mockMvc.perform(get("/api/v1/posts/2/comments"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[1].comment", Matchers.is("commentTestTwo")));
    }

    private List<CommentDto> createCommentList() {
        List<CommentDto> commentDto = new ArrayList<>();

        CommentDto commentDto1 = new CommentDto();
        commentDto1.setCommentId("1");
        commentDto1.setComment("commentTestOne");
        commentDto1.setCommentedBy(new UserDto("123","firstTest","middleTest","lastTest","123",LocalDate.now(),"FEMALE","123","B_POS","aug@gmail.com","Bangalore"));
        commentDto1.setLikesCount(3L);
        commentDto1.setCreatedAt(null);
        commentDto1.setUpdatedAt(null);

        CommentDto commentDto2 = new CommentDto();
        commentDto2.setCommentId("2");
        commentDto2.setComment("commentTestTwo");
        commentDto2.setCommentedBy(new UserDto("1234","SecondTest","middleTest","lastTest","123",LocalDate.now(),"MALE","123","B_POS","aug@gmail.com","Bangalore"));
        commentDto2.setLikesCount(3L);
        commentDto2.setCreatedAt(null);
        commentDto2.setUpdatedAt(null);

        commentDto.add(commentDto1);
        commentDto.add(commentDto2);

        return commentDto;
    }

    @Test
    public void testCreateComment() throws Exception {
        Comment comment = createOneCommentToPost();
        CommentDto commentDto = new CommentDto();

        Mockito.when(commentService.createComment(comment,"1")).thenReturn(commentDto);
        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .content(asJsonString(comment))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    private Comment createOneCommentToPost() {
        Comment comment = new Comment();

        comment.setCommentId("1");
        comment.setComment("Hi");
        comment.setCommentedBy(String.valueOf(new UserDto("123","firstTest","middleTest","lastTest","123",LocalDate.now(),"FEMALE","123","B_POS","aug@gmail.com","Bangalore")));
        return comment;
    }

    @Test
    public void testGetCommentDetails() throws Exception {
        CommentDto commentDto = createOneComment();
        Mockito.when(commentService.isCommentIdExists("1","1")).thenReturn(true);
        Mockito.when(commentService.getCommentDetails("1")).thenReturn(commentDto);

        mockMvc.perform(get("/api/v1/posts/1/comments/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(6)))
                .andExpect(jsonPath("$.comment", Matchers.is("CommentTest")));
    }

    private CommentDto createOneComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setCommentId("1");
        commentDto.setComment("CommentTest");
        commentDto.setCommentedBy(new UserDto("123","firstTest","middleTest","lastTest","123",LocalDate.now(),"FEMALE","123","B_POS","aug@gmail.com","Bangalore"));
        return commentDto;
    }

    @Test
    public void testUpdateComment() throws Exception {
        Comment comment = createOneCommentToUpdate();
        CommentDto commentDto =new CommentDto();


        Mockito.when(commentService.isCommentIdExists("1","2")).thenReturn(true);
        Mockito.when(commentService.updateComment(comment, "2")).thenReturn(commentDto);
        mockMvc.perform(put("/api/v1/posts/1/comments/2")
                        .content(asJsonString(comment))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    private Comment createOneCommentToUpdate() {
        Comment comment = new Comment();
        comment.setCommentId("2");
        comment.setComment("CommentTest");
        comment.setCommentedBy(String.valueOf(new UserDto("123","firstTest","middleTest","lastTest","123",LocalDate.now(),"FEMALE","123","B_POS","aug@gmail.com","Bangalore")));
        comment.setCreatedAt(null);
        comment.setUpdatedAt(null);
        return comment;
    }



    @Test
    public void testDeleteComment() throws Exception {
        Mockito.when(commentService.isCommentIdExists("1","1")).thenReturn(true);
        Mockito.when(commentService.deleteComment("1")).thenReturn(CommentConstant.DELETECOMMENT);
        this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/posts/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCommentsCount() throws Exception {
        Integer count = createCommentsToCount();

        Mockito.when(commentService.getCommentsCount("1")).thenReturn(Long.valueOf(count));

        mockMvc.perform(get("/api/v1/posts/1/comments/count"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private Integer createCommentsToCount() {
        List<Comment> comments = new ArrayList<>();

        Comment comment1 = new Comment();
        Comment comment2 = new Comment();
        Comment comment3 = new Comment();

        comments.add(comment1);
        comments.add(comment2);
        comments.add(comment3);
        return comments.size();
    }

}
