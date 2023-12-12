package com.walther.product_service.service;

import com.walther.product_service.model.dtos.ProductRequest;
import com.walther.product_service.model.dtos.ProductResponse;
import com.walther.product_service.model.entities.Product;

import java.util.List;

public interface ProductService {
    ProductResponse save(ProductRequest productRequest);
    List<ProductResponse> getAllProducts();

    ProductResponse getProductBySku(String sku);
}
