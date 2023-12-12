package com.walther.inventory_service.repository;

import com.walther.inventory_service.model.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findBySku(String sku);

    List<Inventory> findAllBySkuIn(List<String> skus);
}
