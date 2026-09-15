package com.example.demo.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long id);

    List<Order> findByBookId(Long id);

    @Query("select o from Order o join fetch o.user join fetch o.book where o.user.email = :email order by o.createdAt desc")
    List<Order> findByUserEmailWithDetails(@Param("email") String email);
}
