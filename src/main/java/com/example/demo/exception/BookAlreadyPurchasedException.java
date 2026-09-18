package com.example.demo.exception;

public class BookAlreadyPurchasedException extends RuntimeException {
    public BookAlreadyPurchasedException() {
        super("Book is already purchased");
    }
}
