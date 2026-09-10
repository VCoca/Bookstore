package com.example.demo.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Payload for adding a new book")
public record CreateBookRequest(

        @Schema(description = "Book title", example = "Na Drini ćuprija")
        @NotBlank(message = "Title is required")
        @Size(max = 200)
        String title,

        @Schema(description = "Author's full name", example = "Ivo Andrić")
        @NotBlank(message = "Author is required")
        @Size(max = 200)
        String author,

        @Schema(description = "Unique ISBN, 13 digits", example = "9788610010114")
        @NotBlank
        @Pattern(regexp = "\\d{13}", message = "ISBN must be exactly 13 digits")
        String isbn,

        @Schema(description = "Year of publication", example = "1945")
        @NotNull
        @Min(1450)
        @Max(2100)
        Integer publishedYear,

        @Schema(description = "Initial number of copies", example = "10")
        @NotNull
        @PositiveOrZero
        int availableCopies
) {}
