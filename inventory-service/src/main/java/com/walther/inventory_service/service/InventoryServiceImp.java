package com.walther.inventory_service.service;

import com.walther.inventory_service.model.dtos.*;
import com.walther.inventory_service.model.entities.Inventory;
import com.walther.inventory_service.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryServiceImp implements InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public boolean isAvailable(String sku) {
        Optional<Inventory> bySku = inventoryRepository.findBySku(sku);
        return bySku.filter(inventory -> inventory.getQuantity() > 0).isPresent();
    }

    @Override
    public InventoryResponse getInventoryBySku(String sku) {
        Optional<Inventory> bySku = inventoryRepository.findBySku(sku);
        if (bySku.isPresent()){
            Inventory inventory = bySku.get();
            return new InventoryResponse(inventory.getId(), inventory.getSku(), inventory.getQuantity());
        } else {
            throw new IllegalArgumentException("Sku " + sku + " not found");
        }
    }

    @Override
    public BaseResponse areInStock(List<OrderItemRequest> orderItems) {
        List<String> errors = new ArrayList<>();
        List<String> skus = orderItems.stream()
                .map(OrderItemRequest::getSku)
                .toList();
        List<Inventory> inventories = inventoryRepository.findAllBySkuIn(skus);

        orderItems.forEach(orderItem -> {
            Optional<Inventory> first = inventories.stream()
                    .filter(inventory -> inventory.getSku().equals(orderItem.getSku()))
                    .findFirst();
            if (first.isEmpty()){
                errors.add("Sku " + orderItem.getSku() + " not found");
            } else if (first.get().getQuantity() < orderItem.getQuantity()){
                errors.add("Sku " + orderItem.getSku() + " has only " + first.get().getQuantity() + " items in stock");
            }
        });

        return !errors.isEmpty() ? new BaseResponse(errors.toArray(new String[0])) : new BaseResponse(null);
    }

    @Override
    @KafkaListener(topics = "product-topic", groupId = "inventory-service")
    public BaseResponse addProduct(ProductRequest productRequest) {

        Inventory build = Inventory.builder()
                .sku(productRequest.getSku())
                .quantity(0)
                .build();
        inventoryRepository.save(build);
        return new BaseResponse(null);

    }

    @Override
    public InventoryResponse addStock(InventoryRequest inventoryRequest) {
        Optional<Inventory> bySku = inventoryRepository.findBySku(inventoryRequest.getSku());
        if (bySku.isPresent()){
            Inventory inventory = bySku.get();
            inventory.setQuantity(inventory.getQuantity() + inventoryRequest.getQuantity());
            Inventory saved = inventoryRepository.save(inventory);
            return new InventoryResponse(saved.getId(), saved.getSku(), saved.getQuantity());
        } else {
            throw new IllegalArgumentException("Sku " + inventoryRequest.getSku() + " not found");
        }
    }

    @Override
    public BaseResponse updateStock(List<OrderItemRequest> orderItems) {
        for (OrderItemRequest item: orderItems){
            Optional<Inventory> bySku = inventoryRepository.findBySku(item.getSku());
            if (bySku.isPresent()){
                Inventory inventory = bySku.get();
                inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
                inventoryRepository.save(inventory);
            } else {
                throw new IllegalArgumentException("Sku " + item.getSku() + " not found");
            }
        }
        return new BaseResponse(null);
    }

    @Override
    public List<InventoryResponse> getAll() {
        List<Inventory> inventories = inventoryRepository.findAll();
        return inventories.stream()
                .map(inventory -> new InventoryResponse(inventory.getId(), inventory.getSku(), inventory.getQuantity()))
                .toList();
    }
}
