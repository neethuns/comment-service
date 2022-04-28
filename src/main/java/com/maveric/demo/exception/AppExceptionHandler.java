package com.maveric.demo.exception;


import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@RestControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(CommentNotFoundException.class)
    ResponseEntity<ApiError> commentNotFoundHandler(Exception exception, ServletWebRequest request) {
      ApiError apiError = new ApiError();
        apiError.setCode(HttpStatus.NOT_FOUND.toString());
        apiError.setStatus(HttpStatus.NOT_FOUND);
        apiError.setPath(request.getDescription(false));
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setMessage(exception.getLocalizedMessage());

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(CustomFeignException.class)
    ResponseEntity<ApiError> customFeignExceptionHandler(Exception exception, ServletWebRequest request) {
        ApiError apiError = new ApiError();
        apiError.setCode(HttpStatus.SERVICE_UNAVAILABLE.toString());
        apiError.setStatus(HttpStatus.SERVICE_UNAVAILABLE);
        apiError.setPath(request.getDescription(false));
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setMessage(exception.getLocalizedMessage());

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<String> errors = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        ApiError apiError = new ApiError();
        apiError.setStatus(status);
        apiError.setPath(request.getDescription(false));
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setMessage(String.valueOf(errors));
        return new ResponseEntity<>(apiError, headers, HttpStatus.BAD_REQUEST);
    }



}