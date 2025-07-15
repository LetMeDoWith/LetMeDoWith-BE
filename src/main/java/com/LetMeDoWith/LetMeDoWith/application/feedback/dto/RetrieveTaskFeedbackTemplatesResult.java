package com.LetMeDoWith.LetMeDoWith.application.feedback.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto.TaskFeedbackTemplateQueryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "잔소리 템플릿 조회 결과")
public record RetrieveTaskFeedbackTemplatesResult(List<RetrieveTaskFeedbackTemplateDto> templates) {
    public static RetrieveTaskFeedbackTemplatesResult of(List<TaskFeedbackTemplateQueryDto> templates) {
        List<RetrieveTaskFeedbackTemplateDto> dtos = templates.stream()
                .map(t -> new RetrieveTaskFeedbackTemplateDto(
                        t.id(),
                        t.language(),
                        t.message(),
                        t.emojiUrl()))
                .toList();
        return new RetrieveTaskFeedbackTemplatesResult(dtos);
    }

    public record RetrieveTaskFeedbackTemplateDto(
            @Schema(description = "잔소리 템플릿 ID", example = "1") Long id,
            @Schema(description = "잔소리 언어", example = "KR") CountryCode language,
            @Schema(description = "잔소리 메시지", example = "오늘도 열심히 하셨나요?") String message,
            @Schema(description = "잔소리 이모지 URL", example = "https://example.com/emoji.png") String emojiUrl) {
    }
}