package com.example.demo.book;

import com.example.demo.book.dto.BookDto;
import com.example.demo.book.dto.CreateBookRequest;
import com.example.demo.book.dto.DescriptionResponse;
import com.example.demo.book.dto.UpdateBookRequest;
import com.example.demo.order.dto.OrderDto;
import jakarta.validation.Valid;
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

    private final BookService service;

    public BookController(BookService service){
        this.service = service;
    }

    @Override
    @GetMapping
    public Page<BookDto> findAll(@PageableDefault(size = 20, sort = "title") Pageable pageable){
        return service.findAll(pageable);
    }

    @Override
    @GetMapping("/{id}")
    public DescriptionResponse findById(@PathVariable Long id){
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
