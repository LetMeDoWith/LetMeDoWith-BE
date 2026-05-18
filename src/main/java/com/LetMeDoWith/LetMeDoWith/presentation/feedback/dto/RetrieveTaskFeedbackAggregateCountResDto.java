package com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto;

import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveTaskFeedbackAggregateCountResult;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "잔소리 집계 응답")
public record RetrieveTaskFeedbackAggregateCountResDto(
        @ArraySchema(
                        arraySchema = @Schema(description = "템플릿별 잔소리 집계 목록"),
                        schema = @Schema(implementation = AggregateDto.class))
                List<AggregateDto> aggregates) {
    public static RetrieveTaskFeedbackAggregateCountResDto from(RetrieveTaskFeedbackAggregateCountResult result) {
        List<AggregateDto> aggregates = result.aggregates().stream()
                .map(dto -> new AggregateDto(dto.feedbackTemplateId(), dto.count()))
                .toList();
        return new RetrieveTaskFeedbackAggregateCountResDto(aggregates);
    }

    @Schema(name = "TaskFeedbackAggregateCountItem", description = "템플릿별 잔소리 집계 한 건")
    public record AggregateDto(
            @Schema(description = "잔소리 템플릿 ID", example = "1") Long feedbackTemplateId,
            @Schema(description = "해당 템플릿으로 받은 잔소리 개수", example = "50") Long count) {}
}
