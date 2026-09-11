package com.example.demo.payment;

import com.example.demo.payment.dto.PaymentRequest;
import com.example.demo.payment.dto.PaymentResult;

public interface PaymentClient {
    PaymentResult charge(PaymentRequest request);
}
