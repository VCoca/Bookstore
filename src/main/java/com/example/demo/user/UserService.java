package com.example.demo.user;

import com.example.demo.security.JwtService;
import com.example.demo.user.dto.AuthResponse;
import com.example.demo.user.dto.LoginUserRequest;
import com.example.demo.user.dto.RegisterUserRequest;
import com.example.demo.user.dto.UserDto;
import com.example.demo.user.exception.EmailAlreadyUsedException;
import com.example.demo.user.exception.JmbgAlreadyUsedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService){
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserDto register(RegisterUserRequest request){
        if(repository.existsByEmail(request.email())){
            throw new EmailAlreadyUsedException(request.email());
        }
        if(repository.existsByJmbg(request.jmbg())){
            throw new JmbgAlreadyUsedException();
        }

        String hashedPassword = passwordEncoder.encode(request.password());

        User user = new User(
                request.jmbg(),
                request.firstName(),
                request.lastName(),
                UserRole.USER,
                request.email(),
                hashedPassword
        );
        User savedUser = repository.save(user);
        return UserDto.from(savedUser);
    }

    public AuthResponse login(LoginUserRequest request){
        User user = repository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Pogresan e-mail ili lozinka"));

        if(!passwordEncoder.matches(request.password(), user.getPasswordHashed())){
            throw new BadCredentialsException("Pogresan e-mail ili lozinka");
        }

        String token = jwtService.generateToken(request.email(), user.getRole());

        return new AuthResponse(token, UserDto.from(user));
    }
}
