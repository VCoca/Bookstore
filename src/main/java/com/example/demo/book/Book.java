package com.example.demo.book;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 120)
    private String author;

    @Column(nullable = false, unique = true, length = 13)
    private String isbn;

    @Column(nullable = false, name = "published_year")
    private Integer publishedYear;

    @Column(name = "available_copies", nullable = false)
    private Integer availableCopies;

    @Column(precision = 8, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String description;

    protected Book(){
    }

    public Book(String title, String author, String isbn, Integer publishedYear, Integer availableCopies, BigDecimal price, String description) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publishedYear = publishedYear;
        this.availableCopies = availableCopies;
        this.price = price;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(Integer publishedYear) {
        this.publishedYear = publishedYear;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
