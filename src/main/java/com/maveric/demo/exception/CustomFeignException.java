package com.maveric.demo.exception;

public class CustomFeignException extends RuntimeException {
    public CustomFeignException(String msg) {
        super(msg);
    }
}
