package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CancelLikeDowithTaskResDto(
        @Schema(description = "이미 좋아요 취소 처리 여부", example = "true") Boolean isAlreadyCanceled,
        @Schema(description = "좋아요수", example = "102") long likeCount) {}
