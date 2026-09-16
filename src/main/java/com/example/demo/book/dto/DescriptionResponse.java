package com.example.demo.book.dto;

import com.example.demo.book.Book;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record DescriptionResponse(

        @Schema(description = "Database identifier", example = "1")
        Long id,

        @Schema(description = "Book title", example = "Na Drini ćuprija")
        String title,

        @Schema(description = "Author's full name", example = "Ivo Andrić")
        String author,

        @Schema(description = "13-character ISBN", example = "9788610010114")
        String isbn,

        @Schema(description = "Year of publication", example = "1945")
        Integer publishedYear,

        @Schema(description = "Copies currently in stock", example = "3")
        Integer availableCopies,

        @Schema(description = "Price of the book", example = "1000")
        BigDecimal price,

        @Schema(description = "Description of the book", example = "This is a description of the book.")
        String description

) {
    public static DescriptionResponse from(Book book) {
        return new DescriptionResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPublishedYear(),
                book.getAvailableCopies(),
                book.getPrice(),
                book.getDescription()
        );
    }
}
