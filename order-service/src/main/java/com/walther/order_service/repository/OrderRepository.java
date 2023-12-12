package com.walther.order_service.repository;

import com.walther.order_service.model.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByNumberOrder(String numberOrder);
}
