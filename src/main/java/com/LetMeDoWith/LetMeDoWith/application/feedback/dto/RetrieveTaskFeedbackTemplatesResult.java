package com.LetMeDoWith.LetMeDoWith.application.feedback.dto;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.TaskFeedbackTemplateQueryDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "잔소리 템플릿 조회 결과")
public record RetrieveTaskFeedbackTemplatesResult(List<TaskFeedbackTemplateDto> templates) {

    public static RetrieveTaskFeedbackTemplatesResult of(List<TaskFeedbackTemplateQueryDto> templates) {
        List<TaskFeedbackTemplateDto> dtos = templates.stream()
                .map(t -> new TaskFeedbackTemplateDto(t.id(), t.language(), t.message(), t.emojiUrl()))
                .toList();
        return new RetrieveTaskFeedbackTemplatesResult(dtos);
    }
}
