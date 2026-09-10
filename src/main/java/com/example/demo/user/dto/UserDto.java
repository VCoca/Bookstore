package com.example.demo.user.dto;

import com.example.demo.user.User;
import com.example.demo.user.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Public user information")
public record UserDto (

        @Schema(example = "1")
        Long id,

        @Schema(example = "Veljko")
        String ime,

        @Schema(example = "Petrović")
        String prezime,

        @Schema(example = "USER")
        UserRole role,

        @Schema(example = "veljko@example.com")
        String email
) {
    public static UserDto from(User user){
        return new UserDto(
                user.getId(),
                user.getIme(),
                user.getPrezime(),
                user.getRole(),
                user.getEmail()
        );
    }
}
