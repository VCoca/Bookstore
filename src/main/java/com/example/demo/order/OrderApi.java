package com.example.demo.order;

import com.example.demo.book.dto.BookDto;
import com.example.demo.order.dto.OrderDto;
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

@Tag(name = "Orders", description = "Browse orders")
@SecurityRequirement(name = "bearerAuth")
public interface OrderApi {

    @Operation(
            summary = "Get all orders",
            description = "Returns every order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders found"),
            @ApiResponse(responseCode = "403", description = "User must be ADMIN",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    List<OrderDto> findAll();

    @Operation(
            summary = "Get orders from user",
            description = "Returns every order from a specific user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders found"),
            @ApiResponse(responseCode = "403", description = "User must be ADMIN",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    List<OrderDto> findByUserId(
            @Parameter(description = "User ID", example = "1") Long id);

    @Operation(
            summary = "Get orders of a book",
            description = "Returns every order of a specific book")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders found"),
            @ApiResponse(responseCode = "403", description = "User must be ADMIN",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    List<OrderDto> findByBookId(
            @Parameter(description = "Book ID", example = "1") Long id);

    @Operation(
            summary = "Get my orders",
            description = "Returns the authenticated user's purchase history, newest first")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    List<OrderDto> findMyOrders(@Parameter(hidden = true) Authentication auth);
}
