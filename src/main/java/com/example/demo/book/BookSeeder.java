package com.example.demo.book;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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
                new Book("Na Drini ćuprija", "Ivo Andrić", "9788610012345", 1945, 3),
                new Book("Prokleta avlija", "Ivo Andrić", "9788610012352", 1954, 2),
                new Book("Seobe", "Miloš Crnjanski", "9788610012369", 1929, 1),
                new Book("Effective Java", "Joshua Bloch", "9780134685991", 2018, 5),
                new Book("Clean Code", "Robert C. Martin", "9780132350884", 2008, 0)
        ));

    }
}
