package com.walther.product_service.repository;

import com.walther.product_service.model.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

    Product findBySku(String sku);
}
