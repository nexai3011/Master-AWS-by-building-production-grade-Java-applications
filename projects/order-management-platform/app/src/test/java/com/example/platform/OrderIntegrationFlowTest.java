package com.example.platform;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.platform.dto.CreateOrderRequest;
import com.example.platform.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OrderIntegrationFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void shouldUploadOrderDocumentForExistingOrder() throws Exception {
        String orderJson = objectMapper.writeValueAsString(
                new CreateOrderRequest("customer-s3", "server", 1, new BigDecimal("1299.00")));

        String orderResponse = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long orderId = objectMapper.readTree(orderResponse).get("id").asLong();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "invoice.pdf",
                "application/pdf",
                "invoice-bytes".getBytes());

        mockMvc.perform(multipart("/orders/{id}/documents", orderId)
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.fileName").value("invoice.pdf"));
    }
}
