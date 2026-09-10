package com.example.demo.book;

import com.example.demo.book.dto.BookDto;
import com.example.demo.book.dto.CreateBookRequest;
import com.example.demo.book.dto.UpdateBookRequest;
import com.example.demo.order.dto.OrderDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/books")
public class BookController implements BookApi {

    private final BookService service;

    public BookController(BookService service){
        this.service = service;
    }

    @Override
    @GetMapping
    public List<BookDto> findAll(){
        return service.findAll();
    }

    @Override
    @GetMapping("/{id}")
    public BookDto findById(@PathVariable Long id){
        return service.findById(id);
    }

    @Override
    @GetMapping("/search")
    public List<BookDto> searchByTitle(@RequestParam String title){
        return service.searchByTitle(title);
    }

    @Override
    @PostMapping("/{isbn}/buy")
    public OrderDto buyBook(@PathVariable String isbn, Authentication auth) { return service.buyBook(isbn, auth.getName()); }

    @Override
    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto create(@Valid @RequestBody CreateBookRequest request){
        return service.create(request);
    }

    @Override
    @PutMapping("/admin/{id}")
    public BookDto update(@PathVariable Long id,
                          @Valid @RequestBody UpdateBookRequest request){
        return service.update(id, request);
    }

    @Override
    @DeleteMapping("/admin/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        service.delete(id);
    }

}
