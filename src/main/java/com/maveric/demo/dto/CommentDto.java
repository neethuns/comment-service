package com.maveric.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    @Id
    private String commentId;
    private String comment;
    private UserDto commentedBy;
    private Long likesCount=0L;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
