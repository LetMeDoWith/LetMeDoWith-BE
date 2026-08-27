package com.LetMeDoWith.LetMeDoWith.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AwsConfig {

    @Value("${cloud.aws.region}")
    private String region;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
    }

    @Bean
    public S3Presigner s3Presigner(AwsCredentialsProvider awsCredentialsProvider) {
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

    @Bean
    public S3Client s3Client(AwsCredentialsProvider awsCredentialsProvider) {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }
}
