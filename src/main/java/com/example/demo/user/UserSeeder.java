package com.example.demo.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserSeeder implements CommandLineRunner {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserSeeder(UserRepository repository, PasswordEncoder passwordEncoder){
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args){
        if(repository.count() > 0)
            return;

        repository.saveAll(List.of(
                new User("1122334455667", "Ivan", "Ivanovic", UserRole.USER, "ivan@gmail.com", passwordEncoder.encode("ivan1234")),
                new User("1122334455668", "Marko", "Markovic", UserRole.ADMIN, "marko@gmail.com", passwordEncoder.encode("marko1234"))
        ));
    }
}
