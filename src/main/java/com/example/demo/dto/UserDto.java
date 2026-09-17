package com.example.demo.dto;

import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Public user information")
public record UserDto (

        @Schema(example = "1")
        Long id,

        @Schema(example = "Veljko")
        String firstName,

        @Schema(example = "Petrović")
        String lastName,

        @Schema(example = "USER")
        UserRole role,

        @Schema(example = "veljko@example.com")
        String email
) {
    public static UserDto from(User user){
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getEmail()
        );
    }
}
