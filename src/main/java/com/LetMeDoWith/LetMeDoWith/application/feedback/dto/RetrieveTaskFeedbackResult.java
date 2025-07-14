package com.LetMeDoWith.LetMeDoWith.application.feedback.dto;

import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto.DowithTaskFeedbackQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto.TaskFeedbackTemplateQueryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "두윗모드 Task 잔소리 조회 결과")
public record RetrieveTaskFeedbackResult(List<RetrieveTaskFeedbackDto> feedbacks) {

    public static RetrieveTaskFeedbackResult of(
        List<DowithTaskFeedbackQueryDto> feedbacks, List<TaskFeedbackTemplateQueryDto> templates) {
        List<RetrieveTaskFeedbackDto> feedbackDtos = feedbacks.stream()
            .map(feedback -> new RetrieveTaskFeedbackDto(
                feedback.id(),
                feedback.dowithTaskId(),
                feedback.senderId(),
                feedback.senderNickname(),
                feedback.senderProfileImageUrl(),
                feedback.isChecked(),
                templates.stream()
                    .filter(template -> template.id().equals(feedback.taskFeedbackTemplateId()))
                    .findFirst()
                    .get()
            ))
            .toList();

        return new RetrieveTaskFeedbackResult(feedbackDtos);
    }

    public record RetrieveTaskFeedbackDto(
        @Schema(description = "잔소리 ID", example = "1") Long id,
        @Schema(description = "두윗모드 Task ID", example = "12345") Long dowithTaskId,
        @Schema(description = "잔소리 보낸사람 ID", example = "(TSID)") String senderId,
        @Schema(description = "잔소리 받는사람 닉네임", example = "feedbackSender123") String senderNickname,
        @Schema(description = "잔소리 받는사람 프로필 이미지 URL", example = "https://example.com/profile.jpg")
        String senderProfileImageUrl,
        @Schema(description = "잔소리 확인여부", example = "false") Boolean isChecked,

        @Schema(description = "잔소리 템플릿") TaskFeedbackTemplateQueryDto taskFeedbackTemplate
    ) {

    }
}