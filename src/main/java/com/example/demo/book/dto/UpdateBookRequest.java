package com.example.demo.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Payload for updating an existing book")
public record UpdateBookRequest(

        @Schema(description = "Book title", example = "Na Drini ćuprija")
        @NotBlank(message = "Title is required")
        @Size(max = 200)
        String title,

        @Schema(description = "Author's full name", example = "Ivo Andrić")
        @NotBlank(message = "Author is required")
        @Size(max = 120)
        String author,

        @Schema(description = "Year of publication", example = "1945")
        @NotNull
        @Min(1450)
        @Max(2100)
        Integer publishedYear,

        @Schema(description = "Number of copies in stock", example = "10")
        @NotNull
        @PositiveOrZero
        int availableCopies,

        @Schema(description = "Price of the book", example = "1000")
        @NotNull
        @PositiveOrZero
        BigDecimal price
) {}
