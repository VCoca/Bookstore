package com.example.demo.security.exception;

public class TooManyLoginAttemptsException extends RuntimeException {
    public TooManyLoginAttemptsException() {
        super("Too many login attempts, please try again later");
    }
}
