package com.LetMeDoWith.LetMeDoWith.infrastructure.task;

import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileClientTest {

    //    static final DockerImageName LOCAL_STACK_IMAGE =
    // DockerImageName.parse("localstack/localstack:latest");
    //    private static final String BUCKET = "my-test-bucket";
    //    @Container
    //    LocalStackContainer localStackContainer = new
    // LocalStackContainer(LOCAL_STACK_IMAGE).withServices(LocalStackContainer.Service.S3);
    //
    //    private S3Presigner presigner;
    //    private AwsS3FileClient awsS3FileClient;
    //    private S3Client s3Client;
    //
    //    @BeforeAll
    //    void setup() {
    //        // 2) 자격증명 설정
    //        AwsBasicCredentials creds = AwsBasicCredentials.create(
    //                localStackContainer.getAccessKey(), localStackContainer.getSecretKey());
    //
    //        URI endpoint = localStackContainer.getEndpointOverride(LocalStackContainer.Service.S3);
    //
    //        // 3) 실제 S3Client 생성 및 버킷 생성
    //        s3Client = S3Client.builder()
    //                .endpointOverride(endpoint)
    //                .credentialsProvider(StaticCredentialsProvider.create(creds))
    //                .region(Region.of(localStackContainer.getRegion()))
    //                .build();
    //        s3Client.createBucket(CreateBucketRequest.builder().bucket(BUCKET).build());
    //
    //        // 4) S3Presigner 생성
    //        presigner = S3Presigner.builder()
    //                .endpointOverride(endpoint)
    //                .credentialsProvider(StaticCredentialsProvider.create(creds))
    //                .region(Region.of(localStackContainer.getRegion()))
    //                .build();
    //
    //        // 5) AwsS3FileClient 인스턴스 생성 (Spring 없이 직접)
    //        awsS3FileClient = new AwsS3FileClient(presigner);
    //        // @Value 필드 주입
    //        org.springframework.test.util.ReflectionTestUtils
    //                .setField(awsS3FileClient, "bucketName", BUCKET);
    //    }
    //
    //    @Test
    //    void getUploadPresignedUrl_shouldReturnUrlPointingToLocalStack() {
    //        // 6) presigned URL 생성
    //        Duration expires = Duration.ofMinutes(10);
    //        String key = "folder/my-file.txt";
    //        String url = awsS3FileClient.getUploadPresignedUrl(key, expires);
    //
    //        // 7) URL 검증: LocalStack 엔드포인트 + 버킷/키 포함 여부
    ////
    // assertThat(url).startsWith(localStackContainer.getEndpointOverride(LocalStackContainer.Service.S3).toString());
    //        assertThat(url).contains(BUCKET + "/" + key);
    //    }
}
