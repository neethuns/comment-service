package com.maveric.demo.service;

import com.maveric.demo.dto.CommentDto;
import com.maveric.demo.model.Comment;

import java.util.List;


public interface CommentService {


    List<CommentDto> getAllComments(String postId,Integer page,Integer pageSize);
    CommentDto createComment(Comment comment,String postId);
    Long getCommentsCount(String postId);
    CommentDto getCommentDetails(String commentId);
    Boolean isCommentIdExists(String postId,String commentId);

    CommentDto updateComment(Comment comment, String commentId );

    String deleteComment(String commentId);
}
