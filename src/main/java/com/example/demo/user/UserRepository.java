package com.example.demo.user;

import com.example.demo.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByJmbg(String jmbg);

    Optional<User> findByEmail(String email);

    boolean existsByJmbg(String jmbg);

    boolean existsByEmail(String email);
}
