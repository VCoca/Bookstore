package com.example.demo.book;


import com.example.demo.book.dto.BookDto;
import com.example.demo.book.dto.CreateBookRequest;
import com.example.demo.book.dto.UpdateBookRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.Authentication;

import java.util.List;

@Tag(name = "Books", description = "Browse and purchase books")
@SecurityRequirement(name = "bearerAuth")
public interface BookApi {

    @Operation(
            summary = "Get all books",
            description = "Returns every book")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Books found")
    })
    List<BookDto> findAll();

    @Operation(
            summary = "Get a book by ID",
            description = "Returns a single book")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "No book with that ID",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    BookDto findById(
            @Parameter(description = "Book ID", example = "1") Long id);

    @Operation(
            summary = "Get a book by title",
            description = "Returns a list of books that match given title.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Books found")
    })
    List<BookDto> searchByTitle(
            @Parameter(description = "Book title", example = "Na Drini ćuprija") String title);

    @Operation(
            summary = "Purchase a book",
            description = "Decrements the number of available copies and sends email to user that bought it.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book purchased successfully"),
            @ApiResponse(responseCode = "404", description = "No book with that ISBN",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "No more books available",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    BookDto buyBook(
            @Parameter(description = "Book ISBN", example = "9788610010114") String isbn, Authentication auth);

    @Operation(
            summary = "Add a new book",
            description = "Creates a new book and adds it to the database, only admin can do it.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Book with that ISBN already exists",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    BookDto create(CreateBookRequest request);

    @Operation(
            summary = "Updates a book",
            description = "Updates a book with new values, only admin can do it.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                @ApiResponse(responseCode = "404", description = "No book with that ID",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    BookDto update(@Parameter(description = "Book ID", example = "1") Long id, UpdateBookRequest request);

    @Operation(
            summary = "Deletes a book",
            description = "Deletes a book from the database, only admin can do it.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "404", description = "No book with that ID",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    void delete(@Parameter(description = "Book ID", example = "1") Long id);
}
