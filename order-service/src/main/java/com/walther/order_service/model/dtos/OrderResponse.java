package com.walther.order_service.model.dtos;

import java.util.List;
public record OrderResponse (
    Long id,
    String numberOrder,
    List<OrderItemResponse> orderItems
){
}
