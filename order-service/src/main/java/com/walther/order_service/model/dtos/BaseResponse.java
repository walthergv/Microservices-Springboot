package com.walther.order_service.model.dtos;

public record BaseResponse (String[] errorMessages) {
    public boolean hasErrors() {
        return errorMessages != null && errorMessages.length > 0;
    }
}
