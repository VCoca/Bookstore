package com.example.demo.book.dto;

import com.example.demo.book.Book;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A book in the catalogue")
public record BookDto(

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
        Integer availableCopies
) {
    public static BookDto from(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPublishedYear(),
                book.getAvailableCopies()
        );
    }
}
