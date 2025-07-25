package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Task 잔소리 템플릿 조회 DTO")
public record TaskFeedbackTemplateQueryDto(
        @Schema(description = "잔소리 템플릿 ID", example = "1") Long id,
        @Schema(description = "잔소리 언어", example = "KR") CountryCode language,
        @Schema(description = "잔소리 메시지", example = "오늘도 열심히 하셨나요?") String message,
        @Schema(description = "잔소리 이모지 URL", example = "https://example.com/emoji.png") String emojiUrl) {}
