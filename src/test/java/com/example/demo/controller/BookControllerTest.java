package com.example.demo.controller;

import com.example.demo.dto.BookDto;
import com.example.demo.dto.CreateBookRequest;
import com.example.demo.dto.DescriptionResponse;
import com.example.demo.exception.BookNotFoundException;
import com.example.demo.exception.NoMoreBooksException;
import com.example.demo.exceptionHandler.DomainExceptionHandler;
import com.example.demo.service.JwtService;
import com.example.demo.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import(DomainExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    @Autowired MockMvc mvc;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean JwtService jwtService;

    @MockitoBean
    BookService service;

    @Test
    @DisplayName("GET /api/books/{id} returns a book")
    void findByIdReturnsBook() throws Exception {
        when(service.findById(1L)).thenReturn(
                new DescriptionResponse(1L, "Na Drini ćuprija", "Ivo Andrić", "9788610010114",
                        1945, 3, new BigDecimal("1200.00"), "Neki opis."));

        mvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Na Drini ćuprija"))
                .andExpect(jsonPath("$.availableCopies").value(3));
    }

    @Test
    @DisplayName("GET /api/books/{id} returns 404 as ProblemDetail")
    void findByIdReturns404() throws Exception {
        when(service.findById(99L)).thenThrow(new BookNotFoundException(99L));

        mvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value(containsString("99")));
    }

    @Test
    @DisplayName("POST /api/books/admin returns 400 with map of errors")
    void createReturns400OnInvalidBody() throws Exception {
        var invalid = new CreateBookRequest("", "", "nije-isbn", 1200, -5, new BigDecimal("-1"), "Neki opis.");

        mvc.perform(post("/api/books/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").exists())
                .andExpect(jsonPath("$.errors.isbn").exists())
                .andExpect(jsonPath("$.errors.availableCopies").exists());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("POST /api/books/admin returns 201 for valid body")
    void createReturns201() throws Exception {
        var request = new CreateBookRequest("Seobe", "Miloš Crnjanski", "9788610012369",
                1929, 5, new BigDecimal("800.00"), "Neki opis.");
        when(service.create(any())).thenReturn(
                new BookDto(2L, "Seobe", "Miloš Crnjanski", "9788610012369",
                        1929, 5, new BigDecimal("800.00")));

        mvc.perform(post("/api/books/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    @DisplayName("POST returns 409 when there is no more books in stock")
    void buyBookReturns409() throws Exception {
        when(service.buyBook(anyString(), anyString())).thenThrow(new NoMoreBooksException());

        mvc.perform(post("/api/books/9788610010114/buy")
                        .principal(new UsernamePasswordAuthenticationToken(
                                "ivan@gmail.com", null, List.of())))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("DELETE returns 204")
    void deleteReturns204() throws Exception {
        mvc.perform(delete("/api/books/admin/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }
}