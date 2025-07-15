package com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto;

import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveTaskFeedbackResult;
import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveTaskFeedbackResult.RetrieveTaskFeedbackDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "잔소리 목록 조회 응답")
public record RetrieveTaskFeedbacksResDto(
        @Schema(description = "잔소리 목록") List<RetrieveTaskFeedbackDto> feedbacks) {
    public static RetrieveTaskFeedbacksResDto from(RetrieveTaskFeedbackResult result) {
        return new RetrieveTaskFeedbacksResDto(result.feedbacks());
    }
}