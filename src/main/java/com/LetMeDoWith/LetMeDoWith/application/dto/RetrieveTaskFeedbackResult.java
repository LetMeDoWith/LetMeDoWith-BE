package com.LetMeDoWith.LetMeDoWith.application.dto;

import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto.DowithTaskFeedbackQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto.TaskFeedbackTemplateQueryDto;
import java.util.List;

public record RetrieveTaskFeedbackResult(
    List<DowithTaskFeedbackQueryDto> feedbacks,
    List<TaskFeedbackTemplateQueryDto> templates
) {
    
    public static RetrieveTaskFeedbackResult of(
        List<DowithTaskFeedbackQueryDto> feedbacks,
        List<TaskFeedbackTemplateQueryDto> templates
    ) {
        return new RetrieveTaskFeedbackResult(feedbacks, templates);
    }
}