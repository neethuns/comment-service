package com.maveric.demo.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Document(collection="comment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    private String commentId;
    @NotNull(message="CommentedBy can not be blank")
    private String commentedBy;
    @NotNull(message="Comment can not be blank")
    private String comment;
    private String postId;
    private Long likesCount=0L;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;



}
