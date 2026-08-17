package com.example.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Proxy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestSqsConfig {
        @Bean(name = "testSqsClient")
        @org.springframework.context.annotation.Primary
        SqsClient sqsClient() {
            return (SqsClient) Proxy.newProxyInstance(
                    SqsClient.class.getClassLoader(),
                    new Class<?>[]{SqsClient.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("sendMessage")) {
                            return SendMessageResponse.builder().messageId("msg-123").build();
                        }
                        if (method.getName().equals("close")) {
                            return null;
                        }
                        if (method.getName().equals("serviceName")) {
                            return "sqs";
                        }
                        return null;
                    }
            );
        }
    }

    @Test
    void shouldAcceptOrderRequest() throws Exception {
        OrderMessage orderMessage = new OrderMessage(101L, "customer-123", "created");

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderMessage)))
                .andExpect(status().isOk());
    }
}
