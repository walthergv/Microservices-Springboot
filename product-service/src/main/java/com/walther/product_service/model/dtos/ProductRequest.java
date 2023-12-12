package com.walther.product_service.model.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    private String sku;
    private String name;
    private String description;
    private Double price;
}
