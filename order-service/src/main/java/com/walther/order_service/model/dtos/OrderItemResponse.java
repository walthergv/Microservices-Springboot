package com.walther.order_service.model.dtos;

public record OrderItemResponse(
    String sku,
    String description,
    Double price,
    Long quantity,
    Double importe
) {
}
