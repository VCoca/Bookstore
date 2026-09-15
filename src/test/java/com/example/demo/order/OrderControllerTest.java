package com.example.demo.order;

import com.example.demo.book.BookController;
import com.example.demo.exceptionHandler.DomainExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import(DomainExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean OrderService orderService;

    @Test
    @DisplayName("korisnik vidi samo svoje narudžbine")
    void meReturnsOnlyOwnOrders() throws Exception {
        when(orderService.findMyOrders("ivan@gmail.com")).thenReturn(List.of(/* ... */));

        mvc.perform(get("/api/orders/me")
                        .with(user("ivan@gmail.com").roles("USER")))
                .andExpect(status().isOk());

        verify(orderService).findMyOrders("ivan@gmail.com");
    }

    @Test
    @DisplayName("neulogovan korisnik ne može da vidi narudžbine")
    void meRequiresAuthentication() throws Exception {
        mvc.perform(get("/api/orders/me"))
                .andExpect(status().isForbidden());
    }
}