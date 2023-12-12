package com.walther.product_service.service;

import com.walther.product_service.model.dtos.BaseResponse;
import com.walther.product_service.model.dtos.ProductRequest;
import com.walther.product_service.model.dtos.ProductResponse;
import com.walther.product_service.model.entities.Product;
import com.walther.product_service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class ProductServiceImp implements ProductService{
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    private KafkaTemplate<String, ProductRequest> kafkaTemplate;

    @Override
    public ProductResponse save(ProductRequest productRequest) {

        Product build = Product.builder()
                .sku(productRequest.getSku())
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();
        build.setStatus(true);

        Product savedProduct = productRepository.save(build);

        this.kafkaTemplate.send("product-topic", productRequest);

        ProductResponse productResponse = mapToProductResponse(savedProduct);
        if (productResponse == null) throw new IllegalArgumentException("Error al agregar producto al inventario");

        /*BaseResponse result = webClientBuilder.build()
                .post()
                .uri("lb://inventory-service/api/inventory/add-product")
                .bodyValue(productRequest)
                .retrieve()
                .bodyToMono(BaseResponse.class)
                .block();

        if (result == null || result.hasErrors())
            throw new IllegalArgumentException("Error al agregar producto al inventario");
*/
        return productResponse;
    }

    @Override
    public ProductResponse getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku);
        if (product == null) throw new IllegalArgumentException("Product with SKU" + sku + "not found");
        return mapToProductResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        var products = productRepository.findAll();
        return products.stream().map(product -> this.mapToProductResponse(product)).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStatus());
    }
}
