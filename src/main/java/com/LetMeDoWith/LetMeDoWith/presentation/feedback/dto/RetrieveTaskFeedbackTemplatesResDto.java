package com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto;

import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveTaskFeedbackTemplatesResult;
import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveTaskFeedbackTemplatesResult.RetrieveTaskFeedbackTemplateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "잔소리 템플릿 목록 조회 응답")
public record RetrieveTaskFeedbackTemplatesResDto(
        @Schema(description = "템플릿 목록") List<RetrieveTaskFeedbackTemplateDto> templates) {
    public static RetrieveTaskFeedbackTemplatesResDto from(RetrieveTaskFeedbackTemplatesResult result) {
        return new RetrieveTaskFeedbackTemplatesResDto(result.templates());
    }
}