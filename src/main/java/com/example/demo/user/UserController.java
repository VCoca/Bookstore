package com.example.demo.user;

import com.example.demo.user.dto.AuthResponse;
import com.example.demo.user.dto.LoginUserRequest;
import com.example.demo.user.dto.RegisterUserRequest;
import com.example.demo.user.dto.UserDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController implements UserApi {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @Override
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@Valid @RequestBody RegisterUserRequest request){
        return userService.register(request);
    }

    @Override
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginUserRequest request){
        return userService.login(request);
    }
}
