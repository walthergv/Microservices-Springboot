package com.walther.order_service.model.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_item")

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sku;
    private String description;
    private Long quantity;
    private Double price;
    private Double importe;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

}
