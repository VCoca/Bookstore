package com.example.demo.payment.dto;

import com.example.demo.payment.PaymentStatus;

public record PaymentResult(String transactionId, PaymentStatus status) {
}
