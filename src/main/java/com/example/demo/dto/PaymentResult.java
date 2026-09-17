package com.example.demo.dto;

import com.example.demo.payment.PaymentStatus;

public record PaymentResult(String transactionId, PaymentStatus status) {
}
