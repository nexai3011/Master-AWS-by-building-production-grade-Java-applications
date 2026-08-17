package com.example.platform.controller;

import com.example.platform.dto.CreateOrderRequest;
import com.example.platform.dto.OrderResponse;
import com.example.platform.dto.UpdateOrderStatusRequest;
import com.example.platform.entity.OrderEntity;
import com.example.platform.repository.OrderRepository;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import com.example.platform.dto.DocumentUploadResponse;
import com.example.platform.service.DocumentStorageService;
import com.example.platform.service.OrderEventService;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final DocumentStorageService documentStorageService;
    private final OrderEventService orderEventService;

    public OrderController(OrderRepository orderRepository,
                          DocumentStorageService documentStorageService,
                          OrderEventService orderEventService) {
        this.orderRepository = orderRepository;
        this.documentStorageService = documentStorageService;
        this.orderEventService = orderEventService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderEntity entity = new OrderEntity(
                request.customerId(),
                request.product(),
                request.quantity(),
                request.totalAmount());

        OrderEntity saved = orderRepository.save(entity);
        orderEventService.createOrderEvent(saved);

        return ResponseEntity
                .created(URI.create("/orders/" + saved.getId()))
                .body(OrderResponse.fromEntity(saved));
    }

    @GetMapping
    public List<OrderResponse> getOrders() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(OrderResponse::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id,
                                                    @Valid @RequestBody UpdateOrderStatusRequest request) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(request.status());
                    return ResponseEntity.ok(OrderResponse.fromEntity(orderRepository.save(order)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/documents")
    public ResponseEntity<DocumentUploadResponse> uploadDocument(@PathVariable Long id,
                                                               @RequestParam("file") MultipartFile file) throws IOException {
        if (!orderRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        DocumentUploadResponse response = documentStorageService.uploadOrderDocument(id, file);
        return ResponseEntity.ok(response);
    }
}
