package com.LetMeDoWith.LetMeDoWith.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.LetMeDoWith.LetMeDoWith.common.dto.GenerateUploadPresignedUrlsResult;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.FileNamespace;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PresignedUrlServiceTest {

    @Mock
    private FileClient fileClient;

    @InjectMocks
    private PresignedUrlService presignedUrlService;

    @Test
    @DisplayName("[SUCCESS] namespace와 파일명으로 presigned URL 응답을 생성한다.")
    void generateUploadPresignedUrlsSuccess() {
        when(fileClient.getUploadPresignedUrl(startsWith("dowith_task_confirms/"), any()))
                .thenReturn(
                        "https://bucket.s3.ap-northeast-2.amazonaws.com/dowith_task_confirms/00/test_photo1.jpg?signature=abc")
                .thenReturn(
                        "https://bucket.s3.ap-northeast-2.amazonaws.com/dowith_task_confirms/01/test_photo2.png?signature=def");

        GenerateUploadPresignedUrlsResult result = presignedUrlService.generateUploadPresignedUrls(
                FileNamespace.DOWITH_TASK_CONFIRM, List.of("photo1.jpg", "photo2.png"));

        assertThat(result.method()).isEqualTo("POST");
        assertThat(result.presignedUrls()).hasSize(2);
        assertThat(result.publicImageUrls())
                .containsExactly(
                        "https://bucket.s3.ap-northeast-2.amazonaws.com/dowith_task_confirms/00/test_photo1.jpg",
                        "https://bucket.s3.ap-northeast-2.amazonaws.com/dowith_task_confirms/01/test_photo2.png");
        verify(fileClient, times(2)).getUploadPresignedUrl(startsWith("dowith_task_confirms/"), any());
    }

    @Test
    @DisplayName("[FAIL] 허용되지 않은 확장자가 포함되면 예외가 발생한다.")
    void generateUploadPresignedUrlsFailWhenInvalidExtension() {
        assertThatThrownBy(() -> presignedUrlService.generateUploadPresignedUrls(
                        FileNamespace.DOWITH_TASK_CONFIRM, List.of("photo1.pdf", "photo2.jpg")))
                .isInstanceOf(RestApiException.class);
    }

    @Test
    @DisplayName("[FAIL] 빈 파일명 목록이면 예외가 발생한다.")
    void generateUploadPresignedUrlsFailWhenEmptyFileNames() {
        assertThatThrownBy(() ->
                        presignedUrlService.generateUploadPresignedUrls(FileNamespace.DOWITH_TASK_CONFIRM, List.of()))
                .isInstanceOf(RestApiException.class);
    }
}
