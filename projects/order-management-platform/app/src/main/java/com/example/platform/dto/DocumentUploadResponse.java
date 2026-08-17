package com.example.platform.dto;

public record DocumentUploadResponse(Long orderId, String fileName, String storageKey, String status) {
}
