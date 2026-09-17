package com.example.demo.exception;

public class NoMoreBooksException extends RuntimeException{
    public NoMoreBooksException(){
        super("No more available copies");
    }
}
