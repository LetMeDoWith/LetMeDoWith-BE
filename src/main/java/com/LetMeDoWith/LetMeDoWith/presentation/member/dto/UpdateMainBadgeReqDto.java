package com.LetMeDoWith.LetMeDoWith.presentation.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "대표 뱃지 변경 요청")
public record UpdateMainBadgeReqDto(@Schema(description = "대표로 설정할 뱃지 ID", example = "1") Long badgeId) {}
