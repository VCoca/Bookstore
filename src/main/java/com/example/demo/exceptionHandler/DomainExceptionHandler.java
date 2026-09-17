package com.example.demo.exceptionHandler;

import com.example.demo.exception.BookNotFoundException;
import com.example.demo.exception.DuplicateIsbnException;
import com.example.demo.exception.NoMoreBooksException;
import com.example.demo.exception.PaymentDeclinedException;
import com.example.demo.exception.PaymentGatewayException;
import com.example.demo.exception.TooManyLoginAttemptsException;
import com.example.demo.exception.EmailAlreadyUsedException;
import com.example.demo.exception.JmbgAlreadyUsedException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        f -> Optional.ofNullable(f.getDefaultMessage()).orElse("invalid"),
                        (a, b) -> a));

        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Validation failed");
        problem.setDetail("One or more fields are invalid");
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ProblemDetail handleInvalidSort(InvalidDataAccessApiUsageException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Invalid sort parameter");
        problem.setTitle("Invalid request parameter");
        return problem;
    }

    @ExceptionHandler(TooManyLoginAttemptsException.class)
    public ProblemDetail handleTooManyAttempts(TooManyLoginAttemptsException ex) {
        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
        problem.setTitle("Too many requests");
        return problem;
    }
}
