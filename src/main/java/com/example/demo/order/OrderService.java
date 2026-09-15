package com.example.demo.order;

import com.example.demo.order.dto.OrderDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    public List<OrderDto> findAll(){
        return orderRepository.findAll().stream()
                .map(OrderDto::from)
                .toList();
    }

    public List<OrderDto> findByUserId(Long id){
        return orderRepository.findByUserId(id).stream()
                .map(OrderDto::from)
                .toList();
    }

    public List<OrderDto> findByBookId(Long id){
        return orderRepository.findByBookId(id).stream()
                .map(OrderDto::from)
                .toList();
    }

    public List<OrderDto> findMyOrders(String email) {
        return orderRepository.findByUserEmailWithDetails(email).stream()
                .map(OrderDto::from)
                .toList();
    }
}
