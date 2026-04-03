package com.LetMeDoWith.LetMeDoWith.presentation.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GenerateMemberProfileImageUploadPresignedUrlReqDto(
        @Schema(description = "업로드할 프로필 이미지 파일 이름(확장자 포함)", example = "profile.png") String imageFileName) {}
