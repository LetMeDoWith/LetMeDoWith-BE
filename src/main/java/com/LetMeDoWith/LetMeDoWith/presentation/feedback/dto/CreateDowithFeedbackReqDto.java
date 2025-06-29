package com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "두윗모드 잔소리 생성 요청")
public record CreateDowithFeedbackReqDto(
    @Schema(description = "두윗 태스크 ID", example = "12345")
    @NotNull
    Long dowithTaskId,
    
    @Schema(description = "잔소리 템플릿 ID", example = "67890")
    @NotNull
    Long taskFeedbackTemplateId) {
    
}