package com.example.demo.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Payload for registering a new user")
public record RegisterUserRequest(

        @Schema(description = "JMBG, 13 digits")
        @NotBlank
        @Pattern(regexp = "\\d{13}", message = "jmbg must be 13 digits")
        String jmbg,

        @Schema(description = "First name", example = "Veljko")
        @NotBlank(message = "Name is required")
        @Size(max = 50)
        String firstName,

        @Schema(description = "Last name", example = "Petrović")
        @NotBlank(message = "Surname is required")
        @Size(max = 50)
        String lastName,

        @Schema(description = "Email address, used for login and purchase confirmations",
                example = "veljko@example.com")
        @NotBlank(message = "Email is required")
        @Email
        @Size(max = 100)
        String email,

        @Schema(description = "Password, at least 8 characters", example = "lozinka123")
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 72, message = "Lozinka mora imati bar 8 karaktera")
        String password
) {
}
