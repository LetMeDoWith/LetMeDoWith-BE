package com.LetMeDoWith.LetMeDoWith.application.feedback.dto;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.AggregateTaskFeedbacksQueryDto;

import java.util.List;

public record RetrieveTaskFeedbackAggregateResult(
        List<AggregateDto> aggregates
) {
    public static RetrieveTaskFeedbackAggregateResult from(List<AggregateTaskFeedbacksQueryDto> queryDtos) {
        List<AggregateDto> aggregates = queryDtos.stream()
                .map(dto -> new AggregateDto(dto.feedbackTemplateId(), dto.count()))
                .toList();
        return new RetrieveTaskFeedbackAggregateResult(aggregates);
    }

    public record AggregateDto(
            Long feedbackTemplateId,
            Long count
    ) {
    }
}
