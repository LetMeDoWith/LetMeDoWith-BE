package com.LetMeDoWith.LetMeDoWith.presentation.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GenerateMemberProfileImageUploadPresignedUrlResDto(
        @Schema(description = "업로드 후 저장될 프로필 이미지 공개 URL") String publicImageUrl,
        @Schema(description = "실제 업로드 요청에 사용할 presigned URL") String presignedUrl,
        @Schema(description = "업로드에 사용할 HTTP method") String method) {}
