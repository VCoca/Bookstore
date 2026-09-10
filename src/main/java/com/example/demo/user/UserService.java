package com.example.demo.user;

import com.example.demo.security.JwtService;
import com.example.demo.user.dto.AuthResponse;
import com.example.demo.user.dto.LoginUserRequest;
import com.example.demo.user.dto.RegisterUserRequest;
import com.example.demo.user.dto.UserDto;
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
            throw new IllegalArgumentException("Email je vec u upotrebi");
        }
        if(repository.existsByJmbg(request.jmbg())){
            throw new IllegalArgumentException("JMBG je vec u upotrebi");
        }

        String hashedPassword = passwordEncoder.encode(request.password());

        User user = new User(
                request.jmbg(),
                request.ime(),
                request.prezime(),
                UserRole.USER,
                request.email(),
                hashedPassword
        );
        User savedUser = repository.save(user);
        return UserDto.from(savedUser);
    }

    public AuthResponse login(LoginUserRequest request){
        User user = repository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Pogresan e-mail ili lozinka"));

        if(!passwordEncoder.matches(request.password(), user.getPasswordHashed())){
            throw new IllegalArgumentException("Pogresan e-mail ili lozinka");
        }

        String token = jwtService.generateToken(request.email(), user.getRole());

        return new AuthResponse(token, UserDto.from(user));
    }
}
