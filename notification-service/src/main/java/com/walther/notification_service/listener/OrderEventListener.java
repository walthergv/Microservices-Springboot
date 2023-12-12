package com.walther.notification_service.listener;

import com.walther.notification_service.event.OrderEvent;
import com.walther.notification_service.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventListener {


    @KafkaListener(topics = "order-topic")
    public void handleOrdersNotifications(String message) {
        OrderEvent orderEvent = JsonUtils.fromJson(message, OrderEvent.class);

        //Send notification to customer, for example by email, SMS, etc.
        //Notify another service ...

        log.info("Notification Service: Order {}." +
                 " Event Received for order: {} " +
                 " with {} items",
                orderEvent.orderStatus(),
                orderEvent.numberOrder(),
                orderEvent.itemsCount());

        System.out.println("Notification Service: Order Event Received: " + message);
    }

}
