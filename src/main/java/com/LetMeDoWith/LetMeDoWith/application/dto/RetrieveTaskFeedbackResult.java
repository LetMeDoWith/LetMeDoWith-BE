package com.LetMeDoWith.LetMeDoWith.application.dto;

import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto.DowithTaskFeedbackQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto.TaskFeedbackTemplateQueryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "두윗모드 Task 잔소리 조회 결과")
public record RetrieveTaskFeedbackResult(
    @Schema(description = "두윗모드 Task 진소리 목록")
    List<DowithTaskFeedbackQueryDto> feedbacks,
    @Schema(description = "Task 잔소리 템플릿 목록")
    List<TaskFeedbackTemplateQueryDto> templates
) {
    
    public static RetrieveTaskFeedbackResult of(
        List<DowithTaskFeedbackQueryDto> feedbacks,
        List<TaskFeedbackTemplateQueryDto> templates
    ) {
        return new RetrieveTaskFeedbackResult(feedbacks, templates);
    }
}