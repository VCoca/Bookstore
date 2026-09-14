package com.example.demo.order.dto;

import com.example.demo.book.Book;
import com.example.demo.book.dto.BookDto;
import com.example.demo.order.Order;
import com.example.demo.user.User;
import com.example.demo.user.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderDto(

        @Schema(description = "Database identifier", example = "1")
        Long id,

        @Schema(description = "User that bought the book")
        UserDto user,

        @Schema(description = "Book that has been purchased")
        BookDto book,

        @Schema(description = "Price of the book at the time of purchase", example = "1000")
        BigDecimal priceAtPurchase,

        @Schema(description = "Exact date and time of purchase", example = "2026-09-10T11:45:00Z")
        Instant createdAt,

        @Schema(description = "ID of transaction", example = "tx-123")
        String transactionId
) {

    public static OrderDto from(Order order) {
        return new OrderDto(
                order.getId(),
                UserDto.from(order.getUser()),
                BookDto.from(order.getBook()),
                order.getPriceAtPurchase(),
                order.getCreatedAt(),
                order.getTransactionId()
        );
    }
}
