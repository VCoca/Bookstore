package com.example.demo.dto;

import java.math.BigDecimal;

public record PaymentRequest(String userEmail, BigDecimal amount, String reference) {}
