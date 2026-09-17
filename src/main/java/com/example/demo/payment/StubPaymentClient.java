package com.example.demo.payment;

import com.example.demo.dto.PaymentRequest;
import com.example.demo.dto.PaymentResult;
import com.example.demo.exception.PaymentDeclinedException;
import com.example.demo.exception.PaymentGatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class StubPaymentClient implements PaymentClient {

    private static final Logger log = LoggerFactory.getLogger(StubPaymentClient.class);

    private static final BigDecimal DECLINE_ABOVE = new BigDecimal("1000");
    private static final BigDecimal GATEWAY_ERROR_AMOUNT = new BigDecimal("13");

    @Override
    public PaymentResult charge(PaymentRequest request){
        log.info("SIMULIRANA naplata: {} RSD za {}", request.amount(), request.userEmail());

        if(request.amount().compareTo(DECLINE_ABOVE) > 0){
            throw new PaymentDeclinedException("Insufficent funds");
        }
        if(request.amount().compareTo(GATEWAY_ERROR_AMOUNT) == 0){
            throw new PaymentGatewayException("Gateway timeout", null);
        }
        return new PaymentResult(UUID.randomUUID().toString(), PaymentStatus.APPROVED);
    }
}
