package com.example.demo.book;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock BookRepository repository;
    @InjectMocks BookService service;

    private Book book;

    @BeforeEach
    void setUp(){
        book = new Book("Na Drini ćuprija", "Ivo Andrić", "9788610010114", 1945, 3);
        ReflectionTestUtils.setField(book, "id", 1);
    }
}