package com.example.demo.book;

import com.example.demo.book.dto.BookDto;
import com.example.demo.book.dto.CreateBookRequest;
import com.example.demo.book.dto.UpdateBookRequest;
import com.example.demo.book.exception.BookNotFoundException;
import com.example.demo.book.exception.DuplicateIsbnException;
import com.example.demo.book.exception.NoMoreBooksException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository repository;
    public final ApplicationEventPublisher events;

    public BookService(BookRepository repository, ApplicationEventPublisher events){
        this.repository = repository;
        this.events = events;
    }

    public List<BookDto> findAll(){
        return repository.findAll().stream()
                .map(BookDto::from)
                .toList();
    }

    public BookDto findById(Long id) {
        return repository.findById(id)
                .map(BookDto::from)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public List<BookDto> searchByTitle(String fragment) {
        return repository.findByTitleContainingIgnoreCase(fragment).stream()
                .map(BookDto::from)
                .toList();
    }

    @Transactional
    public BookDto buyBook(String isbn, String buyerEmail){

        int updated = repository.decrementCopies(isbn);

        Book book = repository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        if(updated == 0){
            throw new NoMoreBooksException();
        }

        events.publishEvent(new BookPurchasedEvent(buyerEmail, book.getTitle(), book.getIsbn()));

        return BookDto.from(book);
    }

    @Transactional
    public BookDto create(CreateBookRequest request) {
        if (repository.existsByIsbn(request.isbn())) {
            throw new DuplicateIsbnException(request.isbn());
        }
        Book book = new Book(
                request.title(),
                request.author(),
                request.isbn(),
                request.publishedYear(),
                request.availableCopies()
        );
        return BookDto.from(repository.save(book));
    }

    @Transactional
    public BookDto update(Long id, UpdateBookRequest request) {
        Book book = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setPublishedYear(request.publishedYear());
        book.setAvailableCopies(request.availableCopies());

        return BookDto.from(book);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
