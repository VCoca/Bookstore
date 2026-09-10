package com.example.demo.book;

public record BookPurchasedEvent(String email, String title, String isbn) { }
