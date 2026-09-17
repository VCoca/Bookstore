package com.example.demo.controller;

import com.example.demo.controller.api.OrderApi;
import com.example.demo.service.OrderService;
import com.example.demo.dto.OrderDto;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/orders")
public class OrderController implements OrderApi {

    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @Override
    @GetMapping
    public List<OrderDto> findAll(){
        return orderService.findAll();
    }

    @Override
    @GetMapping("/user/{id}")
    public List<OrderDto> findByUserId(@PathVariable Long id){
        return orderService.findByUserId(id);
    }

    @Override
    @GetMapping("/book/{id}")
    public List<OrderDto> findByBookId(@PathVariable Long id){
        return orderService.findByBookId(id);
    }


    @GetMapping("/me")
    public List<OrderDto> findMyOrders(Authentication auth) {
        return orderService.findMyOrders(auth.getName());
    }
}
