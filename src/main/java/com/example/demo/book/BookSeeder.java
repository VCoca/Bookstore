package com.example.demo.book;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
class BookSeeder implements CommandLineRunner {

    private final BookRepository repository;

    BookSeeder(BookRepository repository){
        this.repository = repository;
    }

    @Override
    public void run(String... args){
        if(repository.count() > 0)
            return;

        repository.saveAll(List.of(
                new Book("Na Drini ćuprija", "Ivo Andrić", "9788610012345", 1945, 3, BigDecimal.valueOf(1200)),
                new Book("Prokleta avlija", "Ivo Andrić", "9788610012352", 1954, 2, BigDecimal.valueOf(850)),
                new Book("Seobe", "Miloš Crnjanski", "9788610012369", 1929, 1, BigDecimal.valueOf(900)),
                new Book("Effective Java", "Joshua Bloch", "9780134685991", 2018, 5, BigDecimal.valueOf(1500)),
                new Book("Clean Code", "Robert C. Martin", "9780132350884", 2008, 0, BigDecimal.valueOf(1000))
        ));

    }
}
