package com.example.demo.book;

import com.example.demo.IntegrationTestBase;
import com.example.demo.book.exception.NoMoreBooksException;
import com.example.demo.order.OrderRepository;
import com.example.demo.payment.PaymentClient;
import com.example.demo.payment.PaymentStatus;
import com.example.demo.payment.dto.PaymentResult;
import com.example.demo.payment.exception.PaymentDeclinedException;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import com.example.demo.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class BuyBookIT extends IntegrationTestBase {

    private static final String ISBN = "9788610010114";
    private static final String EMAIL = "ivan@gmail.com";

    @Autowired BookService bookService;
    @Autowired BookRepository bookRepository;
    @Autowired UserRepository userRepository;
    @Autowired OrderRepository orderRepository;

    @MockitoBean PaymentClient paymentClient;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        bookRepository.save(new Book("Na Drini ćuprija", "Ivo Andrić", ISBN,
                1945, 3, new BigDecimal("1200.00")));
        userRepository.save(new User("1122334455667", "Ivan", "Ivanović",
                UserRole.USER, EMAIL, "$2a$10$hash"));
    }

    @Test
    @DisplayName("successful purchase decrements available copies and creates order")
    void successfulPurchasePersistsOrderAndDecrementsStock() {
        when(paymentClient.charge(any()))
                .thenReturn(new PaymentResult("tx-123", PaymentStatus.APPROVED));

        bookService.buyBook(ISBN, EMAIL);

        assertThat(bookRepository.findByIsbn(ISBN).orElseThrow().getAvailableCopies())
                .isEqualTo(2);
        assertThat(orderRepository.findAll()).hasSize(1);
        assertThat(orderRepository.findAll().getFirst().getTransactionId())
                .isEqualTo("tx-123");
    }

    @Test
    @DisplayName("declined payment reverts changes and doesnt create order")
    void declinedPaymentRollsBackStock() {
        when(paymentClient.charge(any()))
                .thenThrow(new PaymentDeclinedException("Insufficient funds"));

        assertThatThrownBy(() -> bookService.buyBook(ISBN, EMAIL))
                .isInstanceOf(PaymentDeclinedException.class);

        assertThat(bookRepository.findByIsbn(ISBN).orElseThrow().getAvailableCopies())
                .isEqualTo(3);
        assertThat(orderRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("purchase of the last copy goes through, next one throws 409")
    void secondPurchaseFailsWhenStockExhausted() {
        Book book = bookRepository.findByIsbn(ISBN).orElseThrow();
        book.setAvailableCopies(1);
        bookRepository.save(book);

        when(paymentClient.charge(any()))
                .thenReturn(new PaymentResult("tx-1", PaymentStatus.APPROVED));

        bookService.buyBook(ISBN, EMAIL);

        assertThatThrownBy(() -> bookService.buyBook(ISBN, EMAIL))
                .isInstanceOf(NoMoreBooksException.class);

        assertThat(orderRepository.findAll()).hasSize(1);
    }
}
