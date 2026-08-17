package com.example.platform.service;

import com.example.platform.dto.DocumentUploadResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class DocumentStorageService {

    private final S3Client s3Client;
    private final String bucketName;
    private final boolean awsEnabled;

    public DocumentStorageService(S3Client s3Client,
                                 @Value("${app.s3.bucket:order-management-documents}") String bucketName,
                                 @Value("${app.aws.enabled:false}") boolean awsEnabled) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.awsEnabled = awsEnabled;
    }

    public DocumentUploadResponse uploadOrderDocument(Long orderId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        String storageKey = "orders/" + orderId + "/documents/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        if (awsEnabled && bucketName != null && !bucketName.isBlank()) {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(storageKey)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes()));
        }

        return new DocumentUploadResponse(
                orderId,
                file.getOriginalFilename(),
                storageKey,
                awsEnabled ? "UPLOADED_TO_S3" : "LOCAL_SIMULATION");
    }
}
