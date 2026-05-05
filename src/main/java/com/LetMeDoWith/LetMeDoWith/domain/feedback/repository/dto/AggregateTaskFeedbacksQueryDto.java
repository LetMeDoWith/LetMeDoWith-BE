package com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto;

public record AggregateTaskFeedbacksQueryDto(
        Long feedbackTemplateId,
        Long count
) {
}
