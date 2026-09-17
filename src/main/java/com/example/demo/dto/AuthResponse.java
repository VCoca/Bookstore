package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT and the authenticated user's details")
public record AuthResponse(

        @Schema(description = "Bearer token — send as 'Authorization: Bearer <token>'",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,

        UserDto user
) { }
