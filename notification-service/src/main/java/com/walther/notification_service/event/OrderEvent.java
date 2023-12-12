package com.walther.notification_service.event;

import com.walther.notification_service.model.enums.OrderStatus;

public record OrderEvent(
        String numberOrder,
        int itemsCount,
        OrderStatus orderStatus
){
}
