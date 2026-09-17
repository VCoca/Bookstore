package com.example.demo.user;

import com.example.demo.security.LoginRateLimiter;
import com.example.demo.security.exception.TooManyLoginAttemptsException;
import com.example.demo.user.dto.AuthResponse;
import com.example.demo.user.dto.LoginUserRequest;
import com.example.demo.user.dto.RegisterUserRequest;
import com.example.demo.user.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController implements UserApi {
    private final UserService userService;
    private final LoginRateLimiter rateLimiter;

    public UserController(UserService userService, LoginRateLimiter rateLimiter){

        this.userService = userService;
        this.rateLimiter = rateLimiter;
    }

    @Override
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@Valid @RequestBody RegisterUserRequest request){
        return userService.register(request);
    }

    @Override
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginUserRequest request, HttpServletRequest httpRequest){

        String key = httpRequest.getRemoteAddr() + ":" + request.email();

        if(!rateLimiter.tryConsume(key)){
            throw new TooManyLoginAttemptsException();
        }
        AuthResponse response = userService.login(request);
        rateLimiter.reset(key);
        return response;
    }
}
