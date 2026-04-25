package com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto;

import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveReceivedTaskFeedbackResult;
import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveReceivedTaskFeedbackResult.ReceivedTaskFeedbackDto;
import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveTaskFeedbackResult.TaskFeedbackTemplateDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "받은 잔소리 목록 조회 응답")
public record RetrieveReceivedDowithTaskFeedbacksResDto(
        @Schema(description = "받은 잔소리 목록") List<ReceivedFeedbackDto> feedbacks) {

    public static RetrieveReceivedDowithTaskFeedbacksResDto from(RetrieveReceivedTaskFeedbackResult result) {
        return new RetrieveReceivedDowithTaskFeedbacksResDto(
                result.feedbacks().stream().map(ReceivedFeedbackDto::from).toList());
    }

    public record ReceivedFeedbackDto(
            @Schema(description = "잔소리 ID", example = "1") Long id,
            @Schema(description = "두윗모드 Task ID", example = "12345") Long dowithTaskId,
            @Schema(description = "두윗모드 Task 제목", example = "저녁 러닝하기") String dowithTaskTitle,
            @Schema(description = "잔소리 보낸사람 ID", example = "(TSID)") String senderId,
            @Schema(description = "잔소리 보낸사람 닉네임", example = "feedbackSender123") String senderNickname,
            @Schema(description = "잔소리 보낸사람 프로필 이미지 URL", example = "https://example.com/profile.jpg")
                    String senderProfileImageUrl,
            @Schema(description = "잔소리 확인여부", example = "false") Boolean isChecked,
            @Schema(description = "잔소리 받은 시각") LocalDateTime receivedAt,
            @Schema(description = "잔소리 템플릿") ReceivedFeedbackTemplateDto taskFeedbackTemplate) {

        public static ReceivedFeedbackDto from(ReceivedTaskFeedbackDto feedback) {
            return new ReceivedFeedbackDto(
                    feedback.id(),
                    feedback.dowithTaskId(),
                    feedback.dowithTaskTitle(),
                    feedback.senderId(),
                    feedback.senderNickname(),
                    feedback.senderProfileImageUrl(),
                    feedback.isChecked(),
                    feedback.receivedAt(),
                    ReceivedFeedbackTemplateDto.from(feedback.taskFeedbackTemplate()));
        }
    }

    public record ReceivedFeedbackTemplateDto(
            @Schema(description = "잔소리 템플릿 ID", example = "1") Long id,
            @Schema(description = "잔소리 템플릿 언어", example = "ko") CountryCode language,
            @Schema(description = "잔소리 템플릿 메시지", example = "잔소리 템플릿 메시지") String message,
            @Schema(description = "잔소리 템플릿 이모지 URL", example = "https://example.com/emoji.png") String emojiUrl) {

        public static ReceivedFeedbackTemplateDto from(TaskFeedbackTemplateDto template) {
            return new ReceivedFeedbackTemplateDto(
                    template.id(), template.language(), template.message(), template.emojiUrl());
        }
    }
}
