package com.LetMeDoWith.LetMeDoWith.application.feedback.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto.TaskFeedbackTemplateQueryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "잔소리 템플릿 조회 결과")
public record RetrieveTaskFeedbackTemplatesResult(List<TaskFeedbackTemplateDto> templates) {
    public static RetrieveTaskFeedbackTemplatesResult of(List<TaskFeedbackTemplateQueryDto> templates) {
        List<TaskFeedbackTemplateDto> dtos = templates.stream()
                .map(t -> new TaskFeedbackTemplateDto(
                        t.id(),
                        t.language(),
                        t.message(),
                        t.emojiUrl()))
                .toList();
        return new RetrieveTaskFeedbackTemplatesResult(dtos);
    }

    public record TaskFeedbackTemplateDto(
            @Schema(description = "잔소리 템플릿 ID", example = "1") Long id,
            @Schema(description = "잔소리 언어", example = "KR") CountryCode language,
            @Schema(description = "잔소리 메시지", example = "오늘도 열심히 하셨나요?") String message,
            @Schema(description = "잔소리 이모지 URL", example = "https://example.com/emoji.png") String emojiUrl) {
    }
}