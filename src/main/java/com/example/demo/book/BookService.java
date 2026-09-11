package com.example.demo.book;

import com.example.demo.book.dto.BookDto;
import com.example.demo.book.dto.CreateBookRequest;
import com.example.demo.book.dto.UpdateBookRequest;
import com.example.demo.book.exception.BookNotFoundException;
import com.example.demo.book.exception.DuplicateIsbnException;
import com.example.demo.book.exception.NoMoreBooksException;
import com.example.demo.order.Order;
import com.example.demo.order.OrderRepository;
import com.example.demo.order.dto.OrderDto;
import com.example.demo.payment.PaymentClient;
import com.example.demo.payment.dto.PaymentRequest;
import com.example.demo.payment.dto.PaymentResult;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import com.example.demo.user.exception.UserNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
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

    public List<BookDto> findAll(){
        return bookRepository.findAll().stream()
                .map(BookDto::from)
                .toList();
    }

    public BookDto findById(Long id) {
        return bookRepository.findById(id)
                .map(BookDto::from)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public List<BookDto> searchByTitle(String fragment) {
        return bookRepository.findByTitleContainingIgnoreCase(fragment).stream()
                .map(BookDto::from)
                .toList();
    }

    @Transactional
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
                request.price()
        );
        return BookDto.from(bookRepository.save(book));
    }

    @Transactional
    public BookDto update(Long id, UpdateBookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setPublishedYear(request.publishedYear());
        book.setAvailableCopies(request.availableCopies());
        book.setPrice(request.price());

        return BookDto.from(book);
    }

    @Transactional
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        bookRepository.deleteById(id);
    }
}
