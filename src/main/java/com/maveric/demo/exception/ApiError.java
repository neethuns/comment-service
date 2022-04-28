package com.maveric.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    private String code;
    private String message;
    private HttpStatus status;
    private String path;
    private LocalDateTime timestamp;


}
