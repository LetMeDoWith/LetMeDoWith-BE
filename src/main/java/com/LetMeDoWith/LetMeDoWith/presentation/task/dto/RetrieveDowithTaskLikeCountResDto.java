package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "두윗 Task 좋아요 수 조회 응답")
public record RetrieveDowithTaskLikeCountResDto(@Schema(description = "좋아요 수", example = "102") long likeCount) {}
