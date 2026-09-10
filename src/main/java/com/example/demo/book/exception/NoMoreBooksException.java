package com.example.demo.book.exception;

public class NoMoreBooksException extends RuntimeException{
    public NoMoreBooksException(){
        super("No more available copies");
    }
}
