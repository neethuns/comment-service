package com.maveric.demo.service;

import com.maveric.demo.constant.CommentConstant;
import com.maveric.demo.dto.CommentDto;
import com.maveric.demo.dto.UserDto;
import com.maveric.demo.exception.CommentNotFoundException;
import com.maveric.demo.exception.CustomFeignException;
import com.maveric.demo.feign.LikeFeign;
import com.maveric.demo.feign.UserFeign;
import com.maveric.demo.model.Comment;
import com.maveric.demo.repo.CommentRepo;

import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest

public class CommentServiceTest {

    @InjectMocks
    CommentServiceImpl commentService;

    @Mock
    CommentRepo commentRepo;

    @Mock
    UserFeign userFeign;
    @Mock
    LikeFeign likeFeign;


    @Test
    void testDeleteComment() {
        Comment comment = new Comment();
        comment.setComment("Comment");
        comment.setCommentedBy("Commented By");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setCommentId("1");
        comment.setPostId("1");
        comment.setUpdatedAt(LocalDateTime.now());
        Mockito.when(commentRepo.findById("1")).thenReturn(Optional.of(comment));
        commentService.deleteComment("1");
        verify(commentRepo, times(1)).deleteById("1");
    }

    @Test
    void testExceptionThrownForCommentNotFoundWhenDeleteCommentById() {
        Mockito.doThrow(CommentNotFoundException.class).when(commentRepo).deleteById(any());
        Exception userNotFoundException = assertThrows(CommentNotFoundException.class, () -> commentService.deleteComment("1"));
        assertTrue(userNotFoundException.getMessage().contains(CommentConstant.COMMENTNOTFOUND));
    }

    @Test
    void testGetCommentsCountBy() {
        List<Comment> comments = new ArrayList<>();
        Comment comment1 = new Comment();
        comment1.setCommentId("1");
        comment1.setComment("good");
        Comment comment2 = new Comment();
        comment2.setCommentId("2");
        comment2.setComment("nice");
        comments.add(comment1);
        comments.add(comment2);

        Mockito.when(commentRepo.findByPostId("1")).thenReturn(Optional.of(comments));
        assertThat(commentService.getCommentsCount("1"));
    }

//    @Test
//    void testExceptionThrownWhenGetCommentsCountNotFound() {
//        Mockito.doThrow(CommentNotFoundException.class).when(commentRepo).findByPostId(any());
//        Exception commentNotFoundException = assertThrows(CommentNotFoundException.class, () -> commentService.getCommentsCount("1"));
//        assertTrue(commentNotFoundException.getMessage().contains(CommentConstant.COMMENTNOTFOUND));
//    }
@Test
void testGetAllComments() {
    when(this.userFeign.getUserDetails((String) any())).thenReturn(ResponseEntity.of(Optional.of(new UserDto())));
    when(this.likeFeign.getLikesCounts((String) any())).thenReturn(3L);
    Comment comment = new Comment();
    comment.setComment("Comment");
    comment.setCommentedBy("Commented By");
    comment.setCreatedAt(LocalDateTime.now());
    comment.setCommentId("1");
    comment.setPostId("1");
    comment.setUpdatedAt(LocalDateTime.now());
    ArrayList<Comment> commentList = new ArrayList<>();
    commentList.add(comment);
    when(this.commentRepo.findByPostId((String) any(), (org.springframework.data.domain.PageRequest) any())).thenReturn(Optional.of(commentList));
    assertEquals(1, this.commentService.getAllComments("1", 1, 3).size());
}
//    @Test
//    void testGetAllComments() {
//        Mockito.when(this.userFeign.getUserDetails(any())).thenReturn(ResponseEntity.of(Optional.of(new UserDto())));
//        Mockito.when(this.likeFeign.getLikesCounts(any())).thenReturn(3L);
//
//        Comment comment = new Comment();
//        comment.setComment("New Comment");
//        comment.setCommentedBy("12");
//        comment.setCreatedAt(LocalDateTime.now());
//        comment.setCommentId("1");
//        comment.setPostId("1");
//        comment.setUpdatedAt(LocalDateTime.now());
//
//        ArrayList<Comment> commentList = new ArrayList<>();
//        commentList.add(comment);
//
//        Mockito.when(this.commentRepo.findByPostId(any(), any())).thenReturn(Optional.of(commentList));
//        assertEquals(1, this.commentService.getAllComments("1", 1, 3).size());
//    }

    @Test
    public void isCommentIdExists() {
        Comment commentSelected = new Comment();
        Comment comment = new Comment();
        ArrayList<Comment> comments = new ArrayList();
        commentSelected.setComment("New Comment");
        commentSelected.setCommentedBy("12");
        commentSelected.setCommentId("1");
        commentSelected.setPostId("1");
        commentSelected.setLikesCount(3L);
        comments.add(commentSelected);
         Mockito.when(commentRepo.findByPostId("1")).thenReturn(Optional.of(comments));
        assertEquals(commentService.isCommentIdExists("1", "1"), true);
    }

    @Test
    public void testGetCommentDetails() {
        UserDto userDto = createOneUserDto();
        Mockito.when(userFeign.getUserDetails(any())).thenReturn(ResponseEntity.of(Optional.of(userDto)));
        Mockito.when(likeFeign.getLikesCounts(any())).thenReturn(3L);
        Comment comment = new Comment();
        CommentDto commentDto = createOneCommentDto();
        comment.setComment("New Comment");
        comment.setCommentedBy("12");
        comment.setCommentId("1");
        comment.setPostId("1");
        comment.setLikesCount(3L);

        Mockito.when(commentRepo.findById("1")).thenReturn(Optional.of(comment));
        assertThat(commentService.getCommentDetails("1")).isEqualTo(commentDto);
        assertEquals(3L, commentDto.getLikesCount());
    }

//
//    @Test
//    void testExceptionThrownWhenCommentNotFoundById() {
//        when(this.commentRepo.findByPostId((String) any(), (org.springframework.data.domain.PageRequest) any())).thenReturn(new ArrayList<>());
//        assertThrows(CommentNotFoundException.class, () -> this.commentService.getAllComments("1", 1, 3));
//    }

    @Test
    void testExceptionThrownWhenFeignConnectionIssueForGetAllComments() {
        when(this.userFeign.getUserDetails(any())).thenReturn(ResponseEntity.of(Optional.of(new UserDto())));
        when(this.likeFeign.getLikesCounts(any())).thenThrow(mock(FeignException.class));

        Comment comment = new Comment();
        comment.setComment("Comment");
        comment.setCommentedBy("Commented By");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setCommentId("1");
        comment.setPostId("1");
        comment.setUpdatedAt(LocalDateTime.now());

        ArrayList<Comment> commentList = new ArrayList<>();
        commentList.add(comment);
        when(this.commentRepo.findByPostId(any(), any())).thenReturn(Optional.of(commentList));
        assertThrows(CustomFeignException.class, () -> this.commentService.getAllComments("1", 1, 3));
    }

    @Test
    void testCreateComment() {
        UserDto userDto = createOneUserDto();
        CommentDto commentDto = createOneCommentDto();
        Mockito.when(this.userFeign.getUserDetails(any())).thenReturn(ResponseEntity.of(Optional.of(userDto)));
        Mockito.when(this.likeFeign.getLikesCounts(any())).thenReturn(3L);
        Comment comment = new Comment();
        comment.setComment("New Comment");
        comment.setCommentedBy("12");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setCommentId("1");
        comment.setPostId("1");
        comment.setUpdatedAt(LocalDateTime.now());
        Mockito.when(this.commentRepo.save(comment)).thenReturn(comment);
        when(this.commentService.createComment(comment, "1")).thenReturn(commentDto);
        assertEquals("New Comment", commentDto.getComment());
        assertEquals(3L, commentDto.getLikesCount());
    }

    private CommentDto createOneCommentDto() {
        CommentDto commentDto = new CommentDto();
        commentDto.setCommentId("1");
        commentDto.setComment("New Comment");

        commentDto.setCommentedBy(new UserDto("12", "First", "Second", "Third", "9090345678", LocalDate.of(1989, 10, 13), "MALE", "12345", "O_POS", "anug@mail.com", "Chennai"));

        commentDto.setLikesCount(3L);
        return commentDto;
    }

    private Comment createOneComment() {
        Comment comment = new Comment();
        comment.setCommentId("1");
        comment.setComment("Comment");
        comment.setPostId("1");
        comment.setCommentedBy("12");

        return comment;
    }

    //    @Test
//    void testExceptionThrownWhenFeignConnectionIssueForCreateComment() {
//        CommentRequest commentRequest = new CommentRequest("1", "Comment", "Commented By");
//        when(this.userFeign.getUserById((String) any())).thenReturn(new UserDto());
//        when(this.likeFeign.getLikesCount((String) any())).thenReturn(3);
//        when(this.commentRepo.save((Comment) any())).thenThrow(mock(FeignException.class));
//        assertThrows(CustomFeignException.class, () -> this.commentService.createComment("1", commentRequest));
//    }
//
//    @Test
//    void testExceptionThrownWhenFeignConnectionIssueForGetCommentDetailsById() {
//        when(this.userFeign.getUserById((String) any())).thenReturn(new UserDto());
//        when(this.likeFeign.getLikesCount((String) any())).thenThrow(mock(FeignException.class));
//
//        Comment comment = new Comment();
//        comment.setComment("Comment");
//        comment.setCommentedBy("Commented By");
//        comment.setCreatedAt(LocalDate.now());
//        comment.setId("1");
//        comment.setPostId("1");
//        comment.setUpdatedAt(LocalDate.now());
//        when(this.commentRepo.findByPostIdAndId((String) any(), (String) any())).thenReturn(comment);
//        assertThrows(CustomFeignException.class, () -> this.commentService.getCommentDetails("1", "1"));
//    }
    private UserDto createOneUserDto() {
        UserDto userDto = new UserDto();
        userDto.setUserId("12");
        userDto.setFirstName("First");
        userDto.setMiddleName("Second");
        userDto.setLastName("Third");
        userDto.setPhoneNumber("9090345678");
        userDto.setEmail("anug@mail.com");
        userDto.setDateOfBirth(LocalDate.of(1989, 10, 13));
        userDto.setEmployeeId("12345");
        userDto.setBloodGroup("O_POS");
        userDto.setGender("MALE");
        userDto.setAddress("Chennai");

        return userDto;
    }

    @Test
    void testUpdateComment() {
        UserDto userDto = createOneUserDto();
        Comment comment = createOneComment();
        CommentDto commentDto = createOneCommentDto();

        Mockito.when(userFeign.getUserDetails(any())).thenReturn(ResponseEntity.of(Optional.of(userDto)));
        Mockito.when(likeFeign.getLikesCounts(any())).thenReturn(3L);
        Optional<Comment> commentSelected = Optional.of(new Comment("1", "12", "Comment", "1", 3L, null, null));
        Mockito.when(commentRepo.findById("1")).thenReturn(commentSelected);
        comment.setComment("New Comment");
        Mockito.when(commentRepo.save(comment)).thenReturn(comment);

        assertThat(commentService.updateComment(comment, "1").getComment()).isEqualTo(commentDto.getComment());
        assertEquals(3L, commentDto.getLikesCount());
    }

//    @Test
//    void testExceptionThrownWhenFeignConnectionIssueForUpdateCommentById() {
//        when(this.userFeign.getUserById((String) any())).thenReturn(new UserDto());
//        when(this.likeFeign.getLikesCount((String) any())).thenReturn(3);
//
//        CommentRequest commentRequest = new CommentRequest("1", "Comment", "Commented By");
//        Comment comment = new Comment();
//        comment.setComment("Comment");
//        comment.setCommentedBy("Commented By");
//        comment.setCreatedAt(LocalDate.now());
//        comment.setId("1");
//        comment.setPostId("1");
//        comment.setUpdatedAt(LocalDate.now());
//        when(this.commentRepo.save((Comment) any())).thenThrow(mock(FeignException.class));
//        when(this.commentRepo.findByPostIdAndId((String) any(), (String) any())).thenReturn(comment);
//        assertThrows(CustomFeignException.class, () -> this.commentService.updateComment("1", commentRequest, "1"));
//    }

}