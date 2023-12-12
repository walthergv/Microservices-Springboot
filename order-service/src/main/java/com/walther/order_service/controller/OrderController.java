package com.walther.order_service.controller;

import com.lowagie.text.DocumentException;
import com.walther.order_service.model.dtos.OrderRequest;
import com.walther.order_service.model.dtos.OrderResponse;
import com.walther.order_service.model.entities.Order;
import com.walther.order_service.service.InvoiceService;
import com.walther.order_service.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private InvoiceService invoiceService;

    @PostMapping
    @CircuitBreaker(name = "order-service", fallbackMethod = "placeOrderFallback")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.placeOrder(orderRequest);
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping("/{numberOrder}")
    public ResponseEntity<OrderResponse> getOrderByNumberOrder(@PathVariable String numberOrder) {
        OrderResponse orderResponse = orderService.getOrderByNumberOrder(numberOrder);
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orderResponses = orderService.getAllOrders();
        return ResponseEntity.ok(orderResponses);
    }

    @GetMapping("/invoice/{orderId}")
    public ResponseEntity<byte[]> getInvoice(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        try {
            ByteArrayInputStream invoice = invoiceService.generateInvoice(order);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=invoice.pdf");
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(invoice.readAllBytes());
        } catch (DocumentException e) {
            throw new RuntimeException("Could not generate invoice", e);
        }
    }

    private ResponseEntity<OrderResponse> placeOrderFallback(OrderRequest orderRequest, Throwable throwable) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}
