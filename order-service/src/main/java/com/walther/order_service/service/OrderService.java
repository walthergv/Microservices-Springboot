package com.walther.order_service.service;

import com.walther.order_service.model.dtos.OrderItemRequest;
import com.walther.order_service.model.dtos.OrderRequest;
import com.walther.order_service.model.dtos.OrderResponse;
import com.walther.order_service.model.entities.Order;

import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest orderRequest);
    OrderResponse getOrderByNumberOrder(String numberOrder);
    List<OrderResponse> getAllOrders();

    Order getOrderById(Long orderId);
}
