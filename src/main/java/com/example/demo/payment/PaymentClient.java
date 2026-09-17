package com.example.demo.payment;

import com.example.demo.dto.PaymentRequest;
import com.example.demo.dto.PaymentResult;

public interface PaymentClient {
    PaymentResult charge(PaymentRequest request);
}
