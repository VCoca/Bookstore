package com.example.demo.service;

import com.example.demo.entity.Book;
import com.example.demo.mail.BookPurchasedEvent;
import com.example.demo.repository.BookRepository;
import com.example.demo.dto.BookDto;
import com.example.demo.dto.CreateBookRequest;
import com.example.demo.dto.DescriptionResponse;
import com.example.demo.dto.UpdateBookRequest;
import com.example.demo.exception.BookNotFoundException;
import com.example.demo.exception.DuplicateIsbnException;
import com.example.demo.exception.NoMoreBooksException;
import com.example.demo.entity.Order;
import com.example.demo.repository.OrderRepository;
import com.example.demo.dto.OrderDto;
import com.example.demo.payment.PaymentClient;
import com.example.demo.dto.PaymentRequest;
import com.example.demo.dto.PaymentResult;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.exception.UserNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    public final ApplicationEventPublisher events;
    private final UserRepository userRepository;
    private final PaymentClient paymentClient;

    public BookService(BookRepository bookRepository, OrderRepository orderRepository, ApplicationEventPublisher events, UserRepository userRepository, PaymentClient paymentClient){
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
        this.events = events;
        this.userRepository = userRepository;
        this.paymentClient = paymentClient;
    }

    public Page<BookDto> findAll(Pageable pageable){
        return bookRepository.findAll(pageable)
                .map(BookDto::from);
    }

    @Cacheable("bookById")
    public DescriptionResponse findById(Long id) {
        return bookRepository.findById(id)
                .map(DescriptionResponse::from)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public List<BookDto> searchByTitle(String fragment) {
        return bookRepository.findByTitleContainingIgnoreCase(fragment).stream()
                .map(BookDto::from)
                .toList();
    }

    @Transactional
    @CacheEvict(value = "bookById", allEntries = true)
    public OrderDto buyBook(String isbn, String buyerEmail){

        int updated = bookRepository.decrementCopies(isbn);

        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        User user = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new UserNotFoundException(buyerEmail));

        if(updated == 0){
            throw new NoMoreBooksException();
        }

        PaymentResult payment = paymentClient.charge(new PaymentRequest(buyerEmail, book.getPrice(), isbn));
        
        Order order = new Order(
                user,
                book,
                book.getPrice(),
                payment.transactionId()
        );

        events.publishEvent(new BookPurchasedEvent(buyerEmail, book.getTitle(), book.getIsbn()));

        return OrderDto.from(orderRepository.save(order));
    }

    @Transactional
    public BookDto create(CreateBookRequest request) {
        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new DuplicateIsbnException(request.isbn());
        }
        Book book = new Book(
                request.title(),
                request.author(),
                request.isbn(),
                request.publishedYear(),
                request.availableCopies(),
                request.price(),
                request.description()
        );
        return BookDto.from(bookRepository.save(book));
    }

    @Transactional
    @CacheEvict(value = "bookById", key = "#id")
    public BookDto update(Long id, UpdateBookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setPublishedYear(request.publishedYear());
        book.setAvailableCopies(request.availableCopies());
        book.setPrice(request.price());
        book.setDescription(request.description());

        return BookDto.from(book);
    }

    @Transactional
    @CacheEvict(value = "bookById", key = "#id")
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        bookRepository.deleteById(id);
    }
}
