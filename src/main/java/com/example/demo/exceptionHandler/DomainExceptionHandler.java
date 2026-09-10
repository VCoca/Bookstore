package com.example.demo.exceptionHandler;

import com.example.demo.book.exception.BookNotFoundException;
import com.example.demo.book.exception.DuplicateIsbnException;
import com.example.demo.book.exception.NoMoreBooksException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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
}
