package com.example.demo.book;

import com.example.demo.book.dto.BookDto;
import com.example.demo.book.dto.CreateBookRequest;
import com.example.demo.security.JwtService;
import com.example.demo.user.SecurityConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import(SecurityConfig.class)
public class BookControllerSecurityTest {

    @Autowired MockMvc mvc;

    @MockitoBean BookService bookService;
    @MockitoBean JwtService jwtService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("admin can create a book")
    void adminCanCreate() throws Exception{
        when(bookService.create(any())).thenReturn(
                new BookDto(1L, "Seobe", "Miloš Crnjanski", "9788610012369",
                        1929, 5, new BigDecimal("800.00")));

        var request = new CreateBookRequest("Seobe", "Miloš Crnjanski", "9788610012369",
                1929, 5, new BigDecimal("800.00"));

        mvc.perform(post("/api/books/admin")
                        .with(user("marko@gmail.com").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("user cannot create a book")
    void userCannotCreate() throws Exception {
        mvc.perform(post("/api/books/admin")
                        .with(user("ivan@gmail.com").roles("USER"))
                        .with(csrf()))
           .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("guest cannot buy a book")
    void anonymousCannotBuy() throws Exception {
        mvc.perform(post("/api/books/9788610010114/buy").with(csrf()))
                .andExpect(status().isForbidden());
    }
}
