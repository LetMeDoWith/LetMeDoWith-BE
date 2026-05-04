package com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto;

import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveTaskFeedbackTemplatesResult;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import io.swagger.v3.oas.annotations.media.Schema;

public record FeedbackTemplateDto(
        @Schema(description = "템플릿 ID") Long id,
        @Schema(description = "템플릿 언어") CountryCode language,
        @Schema(description = "템플릿 메시지") String message,
        @Schema(description = "템플릿 이모지 URL") String emojiUrl) {
    public static FeedbackTemplateDto from(RetrieveTaskFeedbackTemplatesResult.TaskFeedbackTemplateDto template) {
        return new FeedbackTemplateDto(template.id(), template.language(), template.message(), template.emojiUrl());
    }
}
