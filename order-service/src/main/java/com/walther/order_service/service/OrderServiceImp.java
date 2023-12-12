package com.walther.order_service.service;

import com.walther.order_service.event.OrderEvent;
import com.walther.order_service.model.dtos.*;
import com.walther.order_service.model.entities.Order;
import com.walther.order_service.model.entities.OrderItem;
import com.walther.order_service.model.enums.OrderStatus;
import com.walther.order_service.repository.OrderRepository;
import com.walther.order_service.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImp implements OrderService{
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public OrderResponse placeOrder(OrderRequest orderRequest) {

        BaseResponse result = webClientBuilder.build()
                .post()
                .uri("lb://inventory-service/api/inventory/in-stock")
                .bodyValue(orderRequest.getOrderItems())
                .retrieve()
                .bodyToMono(BaseResponse.class)
                .block();

        if (result == null || result.hasErrors()) {
            String errorMessage = "Some items are not available in stock";
            if (result != null && result.errorMessages() != null) {
                errorMessage += ": " + String.join(", ", result.errorMessages());
            }
            throw new IllegalArgumentException(errorMessage);
        }
        Order order = new Order();
        order.setNumberOrder(UUID.randomUUID().toString());

        List<OrderItem> orderItems = orderRequest.getOrderItems().stream()
                .map(orderItemRequest -> {
                    ProductResponse productResponse = webClientBuilder.build()
                            .get()
                            .uri("lb://product-service/api/product/{sku}", orderItemRequest.getSku())
                            .retrieve()
                            .bodyToMono(ProductResponse.class)
                            .block();
                    if (productResponse == null) throw new IllegalArgumentException("Product with SKU" + orderItemRequest.getSku() + "not found");

                    Double price = productResponse.getPrice();
                    String description = productResponse.getDescription();
                    Double importe = price * orderItemRequest.getQuantity();

                    return OrderItem.builder()
                            .sku(orderItemRequest.getSku())
                            .description(description)
                            .quantity(orderItemRequest.getQuantity())
                            .price(price)
                            .importe(importe)
                            .order(order)
                            .build();
                })
                .toList();

        order.setOrderItems(orderItems);
        this.orderRepository.save(order);

        BaseResponse updateResult = webClientBuilder.build()
                .post()
                .uri("lb://inventory-service/api/inventory/update-stock")
                .bodyValue(orderRequest.getOrderItems())
                .retrieve()
                .bodyToMono(BaseResponse.class)
                .block();

        if (updateResult == null || updateResult.hasErrors())
            throw new IllegalArgumentException("There was an error updating the stock");

        // TODO: Send message to order-topic
        this.kafkaTemplate.send(
                "order-topic",
                JsonUtils.toJson(new OrderEvent(
                        order.getNumberOrder(),
                        order.getOrderItems().size(),
                        OrderStatus.PLACED)
                )
        );

        return mapOrderToOrderResponse(order);
    }

    @Override
    public OrderResponse getOrderByNumberOrder(String numberOrder) {
        Order order = orderRepository.findByNumberOrder(numberOrder);
        if (order == null) throw new IllegalArgumentException("Order with numberOrder" + numberOrder + "not found");
        return mapOrderToOrderResponse(order);

    }

    @Override
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(order -> mapOrderToOrderResponse(order))
                .toList();
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order with id " + orderId + " not found"));
    }

    private OrderResponse mapOrderToOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getNumberOrder(),
                order.getOrderItems()
                        .stream()
                        .map(orderItem -> mapOrderItemToOrderItemResponse(orderItem))
                        .toList()
        );
    }

    private OrderItemResponse mapOrderItemToOrderItemResponse(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getSku(),
                orderItem.getDescription(),
                orderItem.getPrice(),
                orderItem.getQuantity(),
                orderItem.getImporte()
        );
    }
}
