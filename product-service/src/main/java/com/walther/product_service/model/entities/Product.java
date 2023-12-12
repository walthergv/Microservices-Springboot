package com.walther.product_service.model.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sku;
    private String name;
    private String description;
    private Double price;
    private Boolean status;
}
