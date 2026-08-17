package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class OrderProducer {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final String queueUrl;

    public OrderProducer(SqsClient sqsClient,
                         ObjectMapper objectMapper,
                         @Value("${app.sqs.queue-url:https://sqs.us-east-1.amazonaws.com/123456789012/order-events}") String queueUrl) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.queueUrl = queueUrl;
    }

    public void sendOrder(OrderMessage orderMessage) {
        try {
            String payload = objectMapper.writeValueAsString(orderMessage);
            sqsClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(payload)
                    .build());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to serialize order message", e);
        }
    }
}
