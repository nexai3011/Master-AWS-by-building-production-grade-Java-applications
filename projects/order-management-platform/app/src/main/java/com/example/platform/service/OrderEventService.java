package com.example.platform.service;

import com.example.platform.entity.OrderEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class OrderEventService {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final String queueUrl;
    private final boolean awsEnabled;

    public OrderEventService(SqsClient sqsClient,
                            ObjectMapper objectMapper,
                            @Value("${app.sqs.queue-url:}") String queueUrl,
                            @Value("${app.aws.enabled:false}") boolean awsEnabled) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.queueUrl = queueUrl;
        this.awsEnabled = awsEnabled;
    }

    public Map<String, Object> createOrderEvent(OrderEntity order) {
        Map<String, Object> event = new java.util.LinkedHashMap<>();
        event.put("eventType", "OrderCreated");
        event.put("orderId", order.getId());
        event.put("customerId", order.getCustomerId());
        event.put("product", order.getProduct());
        event.put("status", order.getStatus().name());

        if (awsEnabled && queueUrl != null && !queueUrl.isBlank()) {
            try {
                sqsClient.sendMessage(SendMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .messageBody(objectMapper.writeValueAsString(event))
                        .build());
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Unable to serialize order event", e);
            }
        }

        return event;
    }
}
