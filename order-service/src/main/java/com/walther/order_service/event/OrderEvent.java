package com.walther.order_service.event;

import com.walther.order_service.model.enums.OrderStatus;

public record OrderEvent (
        String numberOrder,
        int itemsCount,
        OrderStatus orderStatus
){
}
