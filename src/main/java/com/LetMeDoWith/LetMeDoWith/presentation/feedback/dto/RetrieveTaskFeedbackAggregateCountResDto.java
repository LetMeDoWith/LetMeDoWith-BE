package com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto;

import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveTaskFeedbackAggregateCountResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "잔소리 집계 최종 응답")
public record RetrieveTaskFeedbackAggregateCountResDto(
        @Schema(description = "집계된 잔소리 템플릿 메시지", example = "집계된 잔소리 템플릿 메시지") List<AggregateDto> aggregates) {
    public static RetrieveTaskFeedbackAggregateCountResDto from(RetrieveTaskFeedbackAggregateCountResult result) {
        List<AggregateDto> aggregates = result.aggregates().stream()
                .map(dto -> new AggregateDto(dto.feedbackTemplateId(), dto.count()))
                .toList();
        return new RetrieveTaskFeedbackAggregateCountResDto(aggregates);
    }

    public record AggregateDto(
            @Schema(description = "잔소리 템플릿 ID", example = "1") Long feedbackTemplateId,
            @Schema(description = "개수", example = "50") Long count) {}
}
