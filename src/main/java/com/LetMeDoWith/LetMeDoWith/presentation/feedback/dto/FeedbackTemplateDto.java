package com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto;

import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.TaskFeedbackTemplateDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.List;

public record FeedbackTemplateDto(
        @Schema(description = "템플릿 ID") Long id,
        @Schema(description = "템플릿 언어") CountryCode language,
        @Schema(description = "템플릿명 줄바꿈 단위 목록", example = "[\"아직도\", \"안하네\"]") List<String> nameTokens,
        @Schema(description = "템플릿 메시지") String message,
        @Schema(description = "템플릿 이모지 URL") String emojiUrl) {
    public static FeedbackTemplateDto from(TaskFeedbackTemplateDto template) {
        return new FeedbackTemplateDto(
                template.id(),
                template.language(),
                splitNameTokens(template.name()),
                template.message(),
                template.emojiUrl());
    }

    private static List<String> splitNameTokens(String name) {
        return Arrays.stream(name.split("\\|")).map(String::trim).toList();
    }
}
