package com.walther.inventory_service.controller;

import com.walther.inventory_service.model.dtos.*;
import com.walther.inventory_service.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/{sku}")
    public boolean isAvailable(@PathVariable String sku) {
        return inventoryService.isAvailable(sku);
    }

    @PostMapping("/in-stock")
    public BaseResponse areInStock(@RequestBody List<OrderItemRequest> orderItems) {
        return inventoryService.areInStock(orderItems);
    }

    @PostMapping("/update-stock")
    public BaseResponse updateStock(@RequestBody List<OrderItemRequest> orderItems) {
        return inventoryService.updateStock(orderItems);
    }

    @PostMapping("/add-product")
    public BaseResponse addProduct(@RequestBody ProductRequest productRequest){
        return inventoryService.addProduct(productRequest);
    }

    @PostMapping
    public InventoryResponse addStock(@RequestBody InventoryRequest inventoryRequest) {
       return inventoryService.addStock(inventoryRequest);
    }

    @GetMapping("/details/{sku}")
    public InventoryResponse getInventoryBySku(@PathVariable String sku) {
        return inventoryService.getInventoryBySku(sku);
    }

    @GetMapping
    public List<InventoryResponse> getAll() {
        return inventoryService.getAll();
    }
}
