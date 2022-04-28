package com.maveric.demo.controller;

import com.maveric.demo.dto.CommentDto;
import com.maveric.demo.exception.CommentNotFoundException;
import com.maveric.demo.model.Comment;
import com.maveric.demo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.QueryParam;
import java.util.List;

import static com.maveric.demo.constant.CommentConstant.COMMENTNOTFOUND;

@CrossOrigin(value = "*")
@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
public class CommentController {

    @Autowired
    CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable ("postId") String postId, @QueryParam("page") Integer page, @QueryParam("pageSize") Integer pageSize){
        return new ResponseEntity<> (commentService.getAllComments(postId, page, pageSize), HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<CommentDto> createComments(@Valid @RequestBody Comment comment, @PathVariable("postId") String postId)
    {

        return new ResponseEntity<>(commentService.createComment(comment,postId),HttpStatus.CREATED);



    }
    @GetMapping("/count")
    public ResponseEntity<Long> getCommentsCount(@PathVariable("postId") String postId)
    {
        return new ResponseEntity<>(commentService.getCommentsCount(postId), HttpStatus.OK);
    }

    @GetMapping("/counts")
    public Long getCommentsCounts(@PathVariable("postId") String postId)
    {
        return commentService.getCommentsCount(postId);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getCommentDetails(@PathVariable("postId") String postId, @PathVariable("commentId") String commentId)
    {
        if( Boolean.TRUE.equals(commentService.isCommentIdExists(postId,commentId)))
        {  return new ResponseEntity<>(commentService.getCommentDetails(commentId), HttpStatus.OK);}
        else throw new CommentNotFoundException(COMMENTNOTFOUND +commentId );
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@RequestBody Comment comment, @PathVariable("postId") String postId , @PathVariable("commentId") String commentId)
    {   if( Boolean.TRUE.equals(commentService.isCommentIdExists(postId,commentId)))
    {return new ResponseEntity<>(commentService.updateComment(comment, commentId), HttpStatus.OK);}
        else throw new CommentNotFoundException(COMMENTNOTFOUND +commentId );

    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable("postId") String postId, @PathVariable("commentId") String commentId) {
        if( Boolean.TRUE.equals(commentService.isCommentIdExists(postId,commentId)))
        { return new ResponseEntity<>(commentService.deleteComment(commentId), HttpStatus.OK);}
        else throw new CommentNotFoundException(COMMENTNOTFOUND +commentId );

    }

}
