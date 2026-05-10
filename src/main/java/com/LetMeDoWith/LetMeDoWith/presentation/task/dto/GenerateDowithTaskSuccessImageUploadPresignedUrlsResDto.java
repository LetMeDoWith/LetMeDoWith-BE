package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "두윗 Task 인증 이미지 업로드용 presigned URL 발급 응답")
public record GenerateDowithTaskSuccessImageUploadPresignedUrlsResDto(
        @Schema(description = "업로드 완료 후 노출될 공개 이미지 URL 목록 (요청 파일명 순서와 대응)") List<String> publicImageUrls,
        @Schema(description = "실제 업로드에 사용할 presigned URL 목록") List<String> presignedUrls,
        @Schema(description = "업로드 HTTP 메서드 (예: PUT)", example = "PUT") String method) {}
