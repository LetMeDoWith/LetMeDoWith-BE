package com.LetMeDoWith.LetMeDoWith.application.feedback.dto;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.SentDowithTaskFeedbackQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.TaskFeedbackTemplateQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "보낸 잔소리 조회 결과")
public record RetrieveSentTaskFeedbackResult(Long totalCount, List<SentTaskFeedbackDto> feedbacks) {

    public static RetrieveSentTaskFeedbackResult of(
            Long totalCount,
            List<SentDowithTaskFeedbackQueryDto> feedbacks,
            List<TaskFeedbackTemplateQueryDto> templates) {
        List<SentTaskFeedbackDto> feedbackDtos = feedbacks.stream()
                .map(feedback -> new SentTaskFeedbackDto(
                        feedback.id(),
                        feedback.dowithTaskId(),
                        feedback.dowithTaskTitle(),
                        feedback.senderId(),
                        feedback.senderNickname(),
                        feedback.senderProfileImageUrl(),
                        feedback.isChecked(),
                        feedback.dowithTaskStatus(),
                        TaskFeedbackTemplateDto.from(templates.stream()
                                .filter(template -> template.id().equals(feedback.taskFeedbackTemplateId()))
                                .findFirst()
                                .get())))
                .toList();

        return new RetrieveSentTaskFeedbackResult(totalCount, feedbackDtos);
    }

    public record SentTaskFeedbackDto(
            @Schema(description = "잔소리 ID", example = "1") Long id,
            @Schema(description = "두윗모드 Task ID", example = "12345") Long dowithTaskId,
            @Schema(description = "두윗모드 Task 제목", example = "저녁 러닝하기") String dowithTaskTitle,
            @Schema(description = "잔소리 보낸사람 ID", example = "(TSID)") String senderId,
            @Schema(description = "잔소리 보낸사람 닉네임", example = "feedbackSender123") String senderNickname,
            @Schema(description = "잔소리 보낸사람 프로필 이미지 URL", example = "https://example.com/profile.jpg")
                    String senderProfileImageUrl,
            @Schema(description = "잔소리 확인여부", example = "false") Boolean isChecked,
            @Schema(description = "잔소리 대상 두윗모드 Task 상태", example = "WAIT") DowithTaskStatus dowithTaskStatus,
            @Schema(description = "잔소리 템플릿") TaskFeedbackTemplateDto taskFeedbackTemplate) {}
}
