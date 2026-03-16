package com.LetMeDoWith.LetMeDoWith.common.dto;

import java.util.List;

/**
 * 업로드용 presigned URL 발급 결과를 표현합니다.
 *
 * @param publicImageUrls query string이 제거된 공개 URL 목록
 * @param presignedUrls 실제 업로드 요청에 사용할 presigned URL 목록
 * @param method 업로드에 사용할 HTTP method
 */
public record GenerateUploadPresignedUrlsResult(
        List<String> publicImageUrls, List<String> presignedUrls, String method) {}
