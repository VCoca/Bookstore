package com.example.demo.exception;

public class JmbgAlreadyUsedException extends RuntimeException {
    public JmbgAlreadyUsedException() {
        super("JMBG already registered");
    }
}
