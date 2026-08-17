package com.example.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AwsClientsConfig {

    @Bean
    public S3Client s3Client(@Value("${app.aws.region:us-east-1}") String region) {
        return S3Client.builder()
                .region(Region.of(region))
                .build();
    }

    @Bean
    public SqsClient sqsClient(@Value("${app.aws.region:us-east-1}") String region) {
        return SqsClient.builder()
                .region(Region.of(region))
                .build();
    }
}
