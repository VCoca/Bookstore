package com.example.demo.service;

import com.example.demo.dto.BookDto;
import com.example.demo.dto.CreateBookRequest;
import com.example.demo.dto.UpdateBookRequest;
import com.example.demo.exception.BookNotFoundException;
import com.example.demo.exception.DuplicateIsbnException;
import com.example.demo.exception.NoMoreBooksException;
import com.example.demo.entity.Book;
import com.example.demo.entity.Order;
import com.example.demo.mail.BookPurchasedEvent;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.dto.OrderDto;
import com.example.demo.payment.PaymentClient;
import com.example.demo.payment.PaymentStatus;
import com.example.demo.dto.PaymentRequest;
import com.example.demo.dto.PaymentResult;
import com.example.demo.exception.PaymentDeclinedException;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    private static final String ISBN = "9788610010114";
    private static final String EMAIL = "ivan@gmail.com";

    @Mock
    BookRepository bookRepository;
    @Mock UserRepository userRepository;
    @Mock OrderRepository orderRepository;
    @Mock PaymentClient paymentClient;
    @Mock ApplicationEventPublisher events;
    @InjectMocks
    BookService service;

    private Book book;
    private User user;

    @BeforeEach
    void setUp(){

        book = new Book("Na Drini ćuprija", "Ivo Andrić", ISBN, 1945, 3, BigDecimal.valueOf(1200), "Neki opis.");
        ReflectionTestUtils.setField(book, "id", 1L);

        user = new User("1122334455667", "Ivan", "Ivanovic", UserRole.USER, EMAIL, "$2a$10$hash");
        ReflectionTestUtils.setField(user, "id", 1L);

    }

    @Test
    @DisplayName("successful purchase charges money, saves the order and publishes event")
    void buyBookSucceeds() {
        when(bookRepository.decrementCopies(ISBN)).thenReturn(1);
        when(bookRepository.findByIsbn(ISBN)).thenReturn(Optional.of(book));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(paymentClient.charge(any())).thenReturn(new PaymentResult("tx-123", PaymentStatus.APPROVED));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderDto result = service.buyBook(ISBN, EMAIL);

        assertThat(result.transactionId()).isEqualTo("tx-123");
        verify(orderRepository).save(any(Order.class));
        verify(events).publishEvent(any(BookPurchasedEvent.class));
    }

    @Test
    @DisplayName("cancelled payment does not create order and does not publish event")
    void buyBookFailsWhenPaymentDeclined() {
        when(bookRepository.decrementCopies(ISBN)).thenReturn(1);
        when(bookRepository.findByIsbn(ISBN)).thenReturn(Optional.of(book));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(paymentClient.charge(any())).thenThrow(new PaymentDeclinedException("Insufficient funds"));

        assertThatThrownBy(() -> service.buyBook(ISBN, EMAIL))
                .isInstanceOf(PaymentDeclinedException.class);

        verify(orderRepository, never()).save(any());
        verifyNoInteractions(events);
    }

    @Test
    @DisplayName("does not charge if there is no more books in stock")
    void buyBookDoesNotChargeWhenOutOfStock() {
        when(bookRepository.decrementCopies(ISBN)).thenReturn(0);
        when(bookRepository.findByIsbn(ISBN)).thenReturn(Optional.of(book));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.buyBook(ISBN, EMAIL))
                .isInstanceOf(NoMoreBooksException.class);

        verifyNoInteractions(paymentClient);
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("sends exact price and reference to payment service")
    void buyBookSendsCorrectPaymentRequest() {
        when(bookRepository.decrementCopies(ISBN)).thenReturn(1);
        when(bookRepository.findByIsbn(ISBN)).thenReturn(Optional.of(book));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(paymentClient.charge(any())).thenReturn(new PaymentResult("tx-123", PaymentStatus.APPROVED));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        service.buyBook(ISBN, EMAIL);

        ArgumentCaptor<PaymentRequest> captor = ArgumentCaptor.forClass(PaymentRequest.class);
        verify(paymentClient).charge(captor.capture());
        assertThat(captor.getValue().amount()).isEqualByComparingTo(book.getPrice());
        assertThat(captor.getValue().userEmail()).isEqualTo(EMAIL);
        assertThat(captor.getValue().reference()).isEqualTo(ISBN);
    }

    @Test
    @DisplayName("create throws exception for duplicate ISBN")
    void createFailsOnDuplicateIsbn() {
        when(bookRepository.existsByIsbn(ISBN)).thenReturn(true);

        assertThatThrownBy(() -> service.create(new CreateBookRequest(
                "Na Drini ćuprija", "Ivo Andrić", ISBN, 1945, 10, new BigDecimal("1200.00"), "Neki opis.")))
                .isInstanceOf(DuplicateIsbnException.class);

        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("update throws exception when ID doesn't exist")
    void updateFailsOnBookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(1L, new UpdateBookRequest(
                "Seobe", "Miloš Crnjanski", 1946, 5, new BigDecimal("800.00"), "Neki opis.")))
                .isInstanceOf(BookNotFoundException.class);

    }

    @Test
    @DisplayName("update changes fields on existing book")
    void updateChangesFields() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookDto result = service.update(1L, new UpdateBookRequest(
                "Novi naslov", "Novi autor", 2000, 7, new BigDecimal("999.00"), "Neki opis"));

        assertThat(result.title()).isEqualTo("Novi naslov");
        assertThat(book.getTitle()).isEqualTo("Novi naslov");
        assertThat(book.getAvailableCopies()).isEqualTo(7);
        assertThat(book.getPrice()).isEqualByComparingTo("999.00");
    }
}