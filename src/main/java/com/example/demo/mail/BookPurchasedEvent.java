package com.example.demo.mail;

public record BookPurchasedEvent(String email, String title, String isbn) { }
