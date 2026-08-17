package com.example.platform.dto;

import com.example.platform.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(@NotNull(message = "status is required") OrderStatus status) {
}
