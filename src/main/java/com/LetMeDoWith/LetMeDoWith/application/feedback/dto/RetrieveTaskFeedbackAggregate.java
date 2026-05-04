package com.LetMeDoWith.LetMeDoWith.application.feedback.dto;

import com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto.FeedbackTemplateDto;

import java.util.List;

public record RetrieveTaskFeedbackAggregate(
        List<FeedbackTemplateDto> aggregates
) {
}
