package com.maveric.demo.service;

import com.maveric.demo.dto.CommentDto;
import com.maveric.demo.dto.UserDto;
import com.maveric.demo.exception.CommentNotFoundException;
import com.maveric.demo.exception.CustomFeignException;
import com.maveric.demo.feign.LikeFeign;
import com.maveric.demo.feign.UserFeign;
import com.maveric.demo.model.Comment;
import com.maveric.demo.repo.CommentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.maveric.demo.constant.CommentConstant.*;

@Service
public class CommentServiceImpl implements CommentService{

    @Autowired
    CommentRepo commentRepo;

    @Autowired
    UserFeign userFeign;

    @Autowired
    LikeFeign likeFeign;


    @Override
    public List<CommentDto> getAllComments(String postId, Integer page, Integer pageSize) {
      try {
          if (page == null) {
              page = 1;
          }
          if (pageSize == null) {
              pageSize = 10;
          }
          Optional<List<Comment>> commentList = commentRepo.findByPostId(postId, PageRequest.of(page - 1, pageSize));
          if (commentList.get().isEmpty()) {

              throw new CommentNotFoundException(COMMENTNOTFOUND + postId);
          } else {
              List<CommentDto> commentList1 = new ArrayList<>();
              for (Comment comment : commentList.get()) {
                  comment.setLikesCount(likeFeign.getLikesCounts(postId));
                  UserDto userDto = userFeign.getUserDetails(comment.getCommentedBy()).getBody();

                  commentList1.add(new CommentDto(comment.getCommentId(), comment.getComment(), userDto, comment.getLikesCount(), comment.getCreatedAt(), comment.getUpdatedAt()));
              }

              return commentList1;
          }
      }
      catch (feign.FeignException e)
      {
          throw new CustomFeignException(FEIGNEXCEPTON);
      }

      catch (com.netflix.hystrix.exception.HystrixRuntimeException e){
          throw new CustomFeignException(FEIGNEXCEPTON);
      }
    }




    @Override
    public CommentDto createComment(Comment comment,String postId) {

        try {
            comment.setPostId(postId);
            comment.setCreatedAt(LocalDateTime.now());
            comment.setUpdatedAt(LocalDateTime.now());

            UserDto userDto = userFeign.getUserDetails(comment.getCommentedBy()).getBody();
            commentRepo.save(comment);
            return new CommentDto(comment.getCommentId(), comment.getComment(), userDto, comment.getLikesCount(), comment.getCreatedAt(), comment.getUpdatedAt());
        }  catch (feign.FeignException e)
        {
            throw new CustomFeignException(FEIGNEXCEPTON);
        }
        catch (com.netflix.hystrix.exception.HystrixRuntimeException e){
            throw new CustomFeignException(FEIGNEXCEPTON);
        }
    }



    @Override
    public Long getCommentsCount(String postId) {
        Optional<List<Comment>> comment=commentRepo.findByPostId(postId);
        if(comment.get().isEmpty()) {
            throw new CommentNotFoundException(COMMENTNOTFOUND + postId);}
        else
        {
            return comment.get().stream().count();
        }

    }

//

    public CommentDto getCommentDetails(String commentId)
    {try {
        Optional<Comment> comments = commentRepo.findById(commentId);


        if (comments.isPresent()) {
            Comment comment = comments.get();
            comment.setLikesCount(likeFeign.getLikesCounts(commentId));
            UserDto userDto = userFeign.getUserDetails(comment.getCommentedBy()).getBody();
            CommentDto commentDto = new CommentDto(comment.getCommentId(), comment.getComment(), userDto, comment.getLikesCount(), comment.getCreatedAt(), comment.getUpdatedAt());
            return commentDto;
        } else {
            throw new CommentNotFoundException(COMMENTNOTFOUND + commentId);
        }
    }
    catch (feign.FeignException e)
    {
        throw new CustomFeignException(FEIGNEXCEPTON);
    }
    catch (com.netflix.hystrix.exception.HystrixRuntimeException e){
        throw new CustomFeignException(FEIGNEXCEPTON);
    }
    }

    public Boolean isCommentIdExists(String postId,String commentId)
    { Optional<List<Comment>> comment=commentRepo.findByPostId(postId);

        if(comment.get().isEmpty()) { throw new CommentNotFoundException(COMMENTNOTFOUND +postId);}
         else
        {
            List<Comment> commentList = comment.get();
            for (Comment comments : commentList) {
                if (comments.getCommentId().equals(commentId))
                    return true;
            }
            return false;
        }


    }

    @Override
    public CommentDto updateComment(Comment comment, String commentId) {
try
{
        if (commentId.equals(comment.getCommentId())) {
            Optional<Comment> selectedComment = commentRepo.findById(commentId);
            if (selectedComment.isPresent()) {

                Comment commentUpdate = selectedComment.get();
                commentUpdate.setComment(comment.getComment());
                commentUpdate.setUpdatedAt(LocalDateTime.now());
                UserDto userDto = userFeign.getUserDetails(comment.getCommentedBy()).getBody();
                commentRepo.save(commentUpdate);
                return new CommentDto(commentUpdate.getCommentId(), commentUpdate.getComment(), userDto, commentUpdate.getLikesCount(), commentUpdate.getCreatedAt(), commentUpdate.getUpdatedAt());
            } else {
                throw new CommentNotFoundException(COMMENTNOTFOUND + commentId);
            }
        }
        else
        {
            throw new CommentNotFoundException(COMMENTIDMISMATCH);}}
         catch (feign.FeignException e)
    {
        throw new CustomFeignException(FEIGNEXCEPTON);
    }
catch (com.netflix.hystrix.exception.HystrixRuntimeException e){
    throw new CustomFeignException(FEIGNEXCEPTON);
}
    }

    @Override
    public String deleteComment(String commentId) {
        Optional<Comment> commentSelected = commentRepo.findById(commentId);
        if (commentSelected.isPresent()) {
            commentRepo.deleteById(commentId);
            return DELETECOMMENT + commentId;
        } else {
            throw new CommentNotFoundException(COMMENTNOTFOUND + commentId);
        }
    }

}
