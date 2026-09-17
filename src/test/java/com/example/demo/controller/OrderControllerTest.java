package com.example.demo.controller;

import com.example.demo.exceptionHandler.DomainExceptionHandler;
import com.example.demo.service.JwtService;
import com.example.demo.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(DomainExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean JwtService jwtService;

    @MockitoBean
    OrderService orderService;

    @Test
    @DisplayName("user only gets their orders")
    void meReturnsOnlyOwnOrders() throws Exception {
        when(orderService.findMyOrders("ivan@gmail.com")).thenReturn(List.of());

        mvc.perform(get("/api/orders/me")
                        .principal(new UsernamePasswordAuthenticationToken(
                                "ivan@gmail.com", null, List.of())))
                .andExpect(status().isOk());

        verify(orderService).findMyOrders("ivan@gmail.com");
    }

    @Test
    @DisplayName("admin gets all orders")
    void findAllReturnsOrders() throws Exception {
        when(orderService.findAll()).thenReturn(List.of());

        mvc.perform(get("/api/orders")
                        .with(user("marko@gmail.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(orderService).findAll();
    }
}