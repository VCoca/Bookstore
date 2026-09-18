package com.example.demo.controller;

import com.example.demo.controller.api.BookApi;
import com.example.demo.exception.BookAlreadyPurchasedException;
import com.example.demo.service.BookService;
import com.example.demo.dto.BookDto;
import com.example.demo.dto.CreateBookRequest;
import com.example.demo.dto.DescriptionResponse;
import com.example.demo.dto.UpdateBookRequest;
import com.example.demo.dto.OrderDto;
import com.example.demo.service.OrderService;
import jakarta.validation.Valid;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/books")
public class BookController implements BookApi {

    private final BookService bookService;
    private final OrderService orderService;
    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    public BookController(BookService bookService, OrderService orderService){

        this.bookService = bookService;
        this.orderService = orderService;
    }

    @Override
    @GetMapping
    public Page<BookDto> findAll(@PageableDefault(size = 20, sort = "title") Pageable pageable){
        return bookService.findAll(pageable);
    }

    @Override
    @GetMapping("/{id}")
    public DescriptionResponse findById(@PathVariable Long id){
        long start = System.nanoTime();
        DescriptionResponse result = bookService.findById(id);
        log.info("GET /api/books/{} — {} ms", id, (System.nanoTime() - start) / 1_000_000.0);
        return result;
    }

    @Override
    @GetMapping("/search")
    public List<BookDto> searchByTitle(@RequestParam String title){
        return bookService.searchByTitle(title);
    }

    @Override
    @PostMapping("/{isbn}/buy")
    public OrderDto buyBook(@PathVariable String isbn, Authentication auth) { return bookService.buyBook(isbn, auth.getName()); }

    @Override
    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto create(@Valid @RequestBody CreateBookRequest request){
        return bookService.create(request);
    }

    @Override
    @PutMapping("/admin/{id}")
    public BookDto update(@PathVariable Long id,
                          @Valid @RequestBody UpdateBookRequest request){
        return bookService.update(id, request);
    }

    @Override
    @DeleteMapping("/admin/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        if(!orderService.findByBookId(id).isEmpty()){
            throw new BookAlreadyPurchasedException();
        }
        bookService.delete(id);
    }

}
