package com.example.demo.user;

import com.example.demo.user.dto.AuthResponse;
import com.example.demo.user.dto.LoginUserRequest;
import com.example.demo.user.dto.RegisterUserRequest;
import com.example.demo.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ProblemDetail;

@Tag(name = "Users", description = "Login and register")
public interface UserApi {

    @Operation(
            summary = "Register user",
            description = "Adds new user to the database")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Email or JMBG already in use",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    UserDto register(RegisterUserRequest request);

    @Operation(
            summary = "Login user",
            description = "Returns token and user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully logged in"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "Invalid email or password",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "429", description = "Too many login attempts",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    AuthResponse login(LoginUserRequest request, HttpServletRequest httpRequest);
}
