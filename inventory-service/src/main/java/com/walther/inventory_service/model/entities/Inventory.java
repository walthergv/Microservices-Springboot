package com.walther.inventory_service.model.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sku;
    private Integer quantity;
}
