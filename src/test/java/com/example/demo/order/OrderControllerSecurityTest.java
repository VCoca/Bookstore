package com.example.demo.order;

import com.example.demo.security.JwtService;
import com.example.demo.user.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
public class OrderControllerSecurityTest {

    @Autowired MockMvc mvc;

    @MockitoBean JwtService jwtService;
    @MockitoBean OrderService orderService;

    @Test
    @DisplayName("anonymous user cannot access /me")
    void anonymousCannotAccessMe() throws Exception {
        mvc.perform(get("/api/orders/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("regular user cannot list all orders")
    void userCannotListAllOrders() throws Exception {
        mvc.perform(get("/api/orders")
                        .with(user("ivan@gmail.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("regular user can access their own orders")
    void userCanAccessOwnOrders() throws Exception {
        when(orderService.findMyOrders("ivan@gmail.com")).thenReturn(List.of());

        mvc.perform(get("/api/orders/me")
                        .with(user("ivan@gmail.com").roles("USER")))
                .andExpect(status().isOk());
    }
}
