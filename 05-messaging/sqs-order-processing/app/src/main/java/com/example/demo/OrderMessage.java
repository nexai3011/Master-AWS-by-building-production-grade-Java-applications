package com.example.demo;

public record OrderMessage(Long orderId, String customerId, String status) {
}
