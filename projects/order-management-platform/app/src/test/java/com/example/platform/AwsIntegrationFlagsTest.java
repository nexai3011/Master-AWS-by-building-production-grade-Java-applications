package com.example.platform;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.platform.service.DocumentStorageService;
import com.example.platform.service.OrderEventService;
import com.example.platform.entity.OrderEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;

@SpringBootTest
class AwsIntegrationFlagsTest {

    @Autowired
    private DocumentStorageService documentStorageService;

    @Autowired
    private OrderEventService orderEventService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private SqsClient sqsClient;

    @Test
    void shouldRunInLocalSafeModeWithoutAwsConfiguration() throws IOException {
        OrderEntity order = new OrderEntity("customer-local", "keyboard", 1, java.math.BigDecimal.valueOf(99.99));

        var upload = documentStorageService.uploadOrderDocument(99L,
                new MockMultipartFile("file", "receipt.txt", "text/plain", "data".getBytes()));

        var event = orderEventService.createOrderEvent(order);

        assertThat(upload.status()).contains("LOCAL");
        assertThat(event.get("eventType")).isEqualTo("OrderCreated");
        assertThat(s3Client).isNotNull();
        assertThat(sqsClient).isNotNull();
    }
}
