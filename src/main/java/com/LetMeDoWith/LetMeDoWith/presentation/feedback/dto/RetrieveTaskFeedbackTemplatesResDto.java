package com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto;

import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveTaskFeedbackTemplatesResult;
import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveTaskFeedbackTemplatesResult.TaskFeedbackTemplateDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "잔소리 템플릿 목록 조회 응답")
public record RetrieveTaskFeedbackTemplatesResDto(
        @Schema(description = "템플릿 목록") List<RetrieveTaskFeedbackTemplateDto> templates) {

    public static RetrieveTaskFeedbackTemplatesResDto from(RetrieveTaskFeedbackTemplatesResult result) {
        return new RetrieveTaskFeedbackTemplatesResDto(result.templates().stream()
                .map(RetrieveTaskFeedbackTemplateDto::from)
                .toList());
    }

    public record RetrieveTaskFeedbackTemplateDto(
            @Schema(description = "템플릿 ID") Long id,
            @Schema(description = "템플릿 언어") CountryCode language,
            @Schema(description = "템플릿 메시지") String message,
            @Schema(description = "템플릿 이모지 URL") String emojiUrl) {
        public static RetrieveTaskFeedbackTemplateDto from(TaskFeedbackTemplateDto template) {
            return new RetrieveTaskFeedbackTemplateDto(
                    template.id(),
                    template.language(),
                    template.message(),
                    template.emojiUrl());
        }
    }
}