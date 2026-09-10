package com.example.demo.book.exception;

public class BookNotFoundException extends RuntimeException{
    public BookNotFoundException(Long id) {
        super("No book with id " + id);
    }

    public BookNotFoundException(String isbn) {
        super("No book with isbn " + isbn);
    }
}
