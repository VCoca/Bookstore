package com.example.demo.payment;

import com.example.demo.payment.dto.PaymentRequest;
import com.example.demo.payment.dto.PaymentResult;
import com.example.demo.payment.exception.PaymentDeclinedException;
import com.example.demo.payment.exception.PaymentGatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;


@Component
@Profile("!prod")
public class FakePaymentClient implements PaymentClient{

    private static final Logger log = LoggerFactory.getLogger(FakePaymentClient.class);

    @Override
    public PaymentResult charge(PaymentRequest request){
        log.info("FAKE naplata: {} RSD za {}", request.amount(), request.userEmail());

        if(request.amount().compareTo(new BigDecimal("1000")) > 0){
            throw new PaymentDeclinedException("Insufficent funds");
        }
        if(request.amount().compareTo(new BigDecimal("13")) == 0){
            throw new PaymentGatewayException("Gateway timeout", null);
        }
        return new PaymentResult(UUID.randomUUID().toString(), PaymentStatus.APPROVED);
    }
}
