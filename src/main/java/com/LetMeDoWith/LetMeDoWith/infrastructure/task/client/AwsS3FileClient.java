package com.LetMeDoWith.LetMeDoWith.infrastructure.task.client;

import com.LetMeDoWith.LetMeDoWith.application.task.client.FileClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class AwsS3FileClient implements FileClient {

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    public String getUploadPresignedUrl(String keyName, Duration expires) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(expires)
                .putObjectRequest(putObjectRequest)
                .build();
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(putObjectPresignRequest);
        return presignedRequest.url().toString();
    }
}
