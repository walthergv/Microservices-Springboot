package com.walther.inventory_service.service;

import com.walther.inventory_service.model.dtos.*;

import java.util.List;

public interface InventoryService {
    boolean isAvailable(String sku);

    InventoryResponse getInventoryBySku(String sku);

    List<InventoryResponse> getAll();

    BaseResponse areInStock(List<OrderItemRequest> orderItems);

    BaseResponse addProduct(ProductRequest productRequest);

    InventoryResponse addStock(InventoryRequest inventoryRequest);

    BaseResponse updateStock(List<OrderItemRequest> orderItems);
}
