package com.maveric.demo.exception;


public class CommentNotFoundException extends RuntimeException{
    public CommentNotFoundException(String s) {
        super(s);
    }
}