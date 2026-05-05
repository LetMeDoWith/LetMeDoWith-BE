package com.LetMeDoWith.LetMeDoWith.application.feedback.dto;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.DowithTaskFeedbackQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.TaskFeedbackTemplateQueryDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "받은 잔소리 조회 결과")
public record RetrieveReceivedTaskFeedbackResult(Long totalCount, List<ReceivedTaskFeedbackDto> feedbacks) {

    public static RetrieveReceivedTaskFeedbackResult of(
            Long totalCount, List<DowithTaskFeedbackQueryDto> feedbacks, List<TaskFeedbackTemplateQueryDto> templates) {
        List<ReceivedTaskFeedbackDto> feedbackDtos = feedbacks.stream()
                .map(feedback -> new ReceivedTaskFeedbackDto(
                        feedback.id(),
                        feedback.dowithTaskId(),
                        feedback.dowithTaskTitle(),
                        feedback.senderId(),
                        feedback.senderNickname(),
                        feedback.senderProfileImageUrl(),
                        feedback.isChecked(),
                        feedback.receivedAt(),
                        TaskFeedbackTemplateDto.from(templates.stream()
                                .filter(template -> template.id().equals(feedback.taskFeedbackTemplateId()))
                                .findFirst()
                                .get())))
                .toList();

        return new RetrieveReceivedTaskFeedbackResult(totalCount, feedbackDtos);
    }

    public record ReceivedTaskFeedbackDto(
            @Schema(description = "잔소리 ID", example = "1") Long id,
            @Schema(description = "두윗모드 Task ID", example = "12345") Long dowithTaskId,
            @Schema(description = "두윗모드 Task 제목", example = "저녁 러닝하기") String dowithTaskTitle,
            @Schema(description = "잔소리 보낸사람 ID", example = "(TSID)") String senderId,
            @Schema(description = "잔소리 보낸사람 닉네임", example = "feedbackSender123") String senderNickname,
            @Schema(description = "잔소리 보낸사람 프로필 이미지 URL", example = "https://example.com/profile.jpg")
            String senderProfileImageUrl,
            @Schema(description = "잔소리 확인여부", example = "false") Boolean isChecked,
            @Schema(description = "잔소리 받은 시각") LocalDateTime receivedAt,
            @Schema(description = "잔소리 템플릿") TaskFeedbackTemplateDto taskFeedbackTemplate) {
    }
}
