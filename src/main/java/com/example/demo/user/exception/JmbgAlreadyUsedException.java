package com.example.demo.user.exception;

public class JmbgAlreadyUsedException extends RuntimeException {
    public JmbgAlreadyUsedException() {
        super("JMBG already registered");
    }
}
