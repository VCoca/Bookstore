package com.example.demo.repository;

import com.example.demo.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByAuthorIgnoreCase(String author);

    List<Book> findByTitleContainingIgnoreCase(String fragment);

    List<Book> findByAvailableCopiesGreaterThan(int minimum);

    boolean existsByIsbn(String isbn);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Book b SET b.availableCopies = b.availableCopies - 1 " +
            "WHERE b.isbn = :isbn AND b.availableCopies > 0")
    int decrementCopies(@Param("isbn") String isbn);
}
