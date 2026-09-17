package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Login credentials")
public record LoginUserRequest(

        @Schema(example = "veljko@example.com")
        @NotBlank(message = "Email is required")
        @Email
        String email,

        @Schema(example = "lozinka123")
        @NotBlank(message = "Password is required")
        String password
) {
}
