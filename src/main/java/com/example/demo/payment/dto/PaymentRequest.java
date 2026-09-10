package com.example.demo.payment.dto;

import java.math.BigDecimal;

public record PaymentRequest(String userEmail, BigDecimal amount, String reference) {}
