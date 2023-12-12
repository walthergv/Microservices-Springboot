package com.walther.product_service.controller;

import com.walther.product_service.model.dtos.ProductRequest;
import com.walther.product_service.model.dtos.ProductResponse;
import com.walther.product_service.model.entities.Product;
import com.walther.product_service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProductResponse> saveProduct(@RequestBody ProductRequest productRequest){
        ProductResponse save = productService.save(productRequest);
        return ResponseEntity.ok(save);
    }

    @GetMapping("/{sku}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku){
        ProductResponse productResponse = this.productService.getProductBySku(sku);
        return ResponseEntity.ok(productResponse);
    }


    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<ProductResponse> getAllProducts(){
        return this.productService.getAllProducts();
    }
}
