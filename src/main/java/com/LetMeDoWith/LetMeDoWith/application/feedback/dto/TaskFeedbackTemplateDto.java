package com.LetMeDoWith.LetMeDoWith.application.feedback.dto;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.TaskFeedbackTemplateQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "잔소리 템플릿 공통 DTO")
public record TaskFeedbackTemplateDto(
        @Schema(description = "잔소리 템플릿 ID", example = "1") Long id,
        @Schema(description = "잔소리 템플릿 언어", example = "ko") CountryCode language,
        @Schema(description = "잔소리명", example = "") String name,
        @Schema(description = "잔소리 템플릿 메시지", example = "잔소리 템플릿 메시지") String message,
        @Schema(description = "잔소리 템플릿 이모지 URL", example = "https://example.com/emoji.png") String emojiUrl) {
    public static TaskFeedbackTemplateDto from(TaskFeedbackTemplateQueryDto template) {
        return new TaskFeedbackTemplateDto(
                template.id(), template.language(), template.name(), template.message(), template.emojiUrl());
    }
}
