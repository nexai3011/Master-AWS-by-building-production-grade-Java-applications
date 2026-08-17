package com.example.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotBlank(message = "customerId is required") String customerId,
        @NotBlank(message = "product is required") String product,
        @NotNull(message = "quantity is required") @Positive(message = "quantity must be positive") Integer quantity,
        @NotNull(message = "totalAmount is required") @Positive(message = "totalAmount must be positive") BigDecimal totalAmount) {
}
