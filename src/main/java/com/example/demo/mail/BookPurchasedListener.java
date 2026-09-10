package com.example.demo.mail;

import com.example.demo.book.BookPurchasedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class BookPurchasedListener {

    private final EmailService emailService;

    public BookPurchasedListener(EmailService emailService){
        this.emailService = emailService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(BookPurchasedEvent event){
        emailService.sendBuyMail(event.email(), event.title(), event.isbn());
    }
}
