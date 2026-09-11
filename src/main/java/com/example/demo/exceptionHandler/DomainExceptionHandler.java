package com.example.demo.exceptionHandler;

import com.example.demo.book.exception.BookNotFoundException;
import com.example.demo.book.exception.DuplicateIsbnException;
import com.example.demo.book.exception.NoMoreBooksException;
import com.example.demo.payment.exception.PaymentDeclinedException;
import com.example.demo.payment.exception.PaymentGatewayException;
import com.example.demo.user.exception.EmailAlreadyUsedException;
import com.example.demo.user.exception.JmbgAlreadyUsedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DomainExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ProblemDetail handleBookNotFound(BookNotFoundException ex){
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Book not found");
        return problem;
    }

    @ExceptionHandler(DuplicateIsbnException.class)
    public ProblemDetail handleDuplicateIsbn(DuplicateIsbnException ex){
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Duplicate ISBN");
        return problem;
    }

    @ExceptionHandler(NoMoreBooksException.class)
    public ProblemDetail handleNoMoreBooks(NoMoreBooksException ex){
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("No more books");
        return problem;
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ProblemDetail handleEmailUsed(EmailAlreadyUsedException ex){
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Email already used");
        return problem;
    }

    @ExceptionHandler(JmbgAlreadyUsedException.class)
    public ProblemDetail handleJMBGUsed(JmbgAlreadyUsedException ex){
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("JMBG already used");
        return problem;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setTitle("Authentication failed");
        return problem;
    }

    @ExceptionHandler(PaymentDeclinedException.class)
    public ProblemDetail handleDeclined(PaymentDeclinedException ex) {
        var p = ProblemDetail.forStatusAndDetail(HttpStatus.PAYMENT_REQUIRED, ex.getMessage());
        p.setTitle("Payment declined");
        return p;
    }

    @ExceptionHandler(PaymentGatewayException.class)
    public ProblemDetail handleGateway(PaymentGatewayException ex) {
        var p = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY,
                "Payment provider is currently unavailable, please try again");
        p.setTitle("Payment provider unavailable");
        return p;
    }
}
