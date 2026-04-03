package com.LetMeDoWith.LetMeDoWith.common.service;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.INVALID_REQUEST;

import com.LetMeDoWith.LetMeDoWith.common.dto.GenerateUploadPresignedUrlsResult;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.FileNamespace;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.util.FileKeyUtil;
import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
/**
 * 업로드 대상 파일명과 namespace를 받아 S3 업로드용 presigned URL 응답을 생성합니다.
 *
 * <p>비즈니스 도메인 검증은 각 도메인 서비스가 담당하고, 이 서비스는 공통 파일명 검증, key 생성,
 * presigned/public URL 조립만 담당합니다.
 */
public class PresignedUrlService {

    private static final Duration DEFAULT_UPLOAD_PRESIGNED_URL_EXPIRES = Duration.ofSeconds(30);
    private static final Pattern VALID_IMAGE_FILE_NAME_PATTERN =
            Pattern.compile("(?i)^.+\\.(jpg|jpeg|png|gif|bmp|webp)$");
    private static final String UPLOAD_METHOD = "POST";

    private final FileClient fileClient;

    /**
     * namespace와 파일명 목록을 기준으로 업로드용 presigned URL 응답을 생성합니다.
     *
     * @param fileNamespace 업로드 파일이 속한 namespace
     * @param fileNames 업로드할 원본 파일명 목록
     * @return public URL, presigned URL, 업로드 method를 포함한 결과
     */
    public GenerateUploadPresignedUrlsResult generateUploadPresignedUrls(
            FileNamespace fileNamespace, List<String> fileNames) {

        validateImageFileNames(fileNames);

        List<String> keys = FileKeyUtil.generateKeys(fileNamespace, fileNames);
        List<String> presignedUrls = keys.stream()
                .map(key -> fileClient.getUploadPresignedUrl(key, DEFAULT_UPLOAD_PRESIGNED_URL_EXPIRES))
                .toList();
        List<String> publicImageUrls = presignedUrls.stream()
                .map(PresignedUrlService::extractPublicUrl)
                .toList();

        return new GenerateUploadPresignedUrlsResult(publicImageUrls, presignedUrls, UPLOAD_METHOD);
    }

    /**
     * 이미지 업로드에 허용된 파일명인지 검증합니다.
     *
     * @param fileNames 업로드할 파일명 목록
     */
    private void validateImageFileNames(List<String> fileNames) {
        if (fileNames == null
                || fileNames.isEmpty()
                || fileNames.stream()
                        .anyMatch(name ->
                                !VALID_IMAGE_FILE_NAME_PATTERN.matcher(name).matches())) {
            throw new RestApiException(INVALID_REQUEST);
        }
    }

    /**
     * presigned URL에서 서명 query string을 제거해 공개 접근용 URL 형태로 변환합니다.
     *
     * @param presignedUrl presigned URL
     * @return query string이 제거된 공개 URL
     */
    private static String extractPublicUrl(String presignedUrl) {
        return presignedUrl.contains("?") ? presignedUrl.substring(0, presignedUrl.indexOf('?')) : presignedUrl;
    }
}
