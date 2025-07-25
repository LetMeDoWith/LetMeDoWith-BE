package com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto;

import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveTaskFeedbackResult;
import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveTaskFeedbackResult.TaskFeedbackDto;
import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveTaskFeedbackResult.TaskFeedbackTemplateDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "잔소리 목록 조회 응답")
public record RetrieveTaskFeedbacksResDto(@Schema(description = "잔소리 목록") List<RetrieveTaskFeedbackDto> feedbacks) {
    public static RetrieveTaskFeedbacksResDto from(RetrieveTaskFeedbackResult result) {
        return new RetrieveTaskFeedbacksResDto(
                result.feedbacks().stream().map(RetrieveTaskFeedbackDto::from).toList());
    }

    public record RetrieveTaskFeedbackDto(
            @Schema(description = "잔소리 ID", example = "1") Long id,
            @Schema(description = "두윗모드 Task ID", example = "12345") Long dowithTaskId,
            @Schema(description = "잔소리 보낸사람 ID", example = "(TSID)") String senderId,
            @Schema(description = "잔소리 받는사람 닉네임", example = "feedbackSender123") String senderNickname,
            @Schema(description = "잔소리 받는사람 프로필 이미지 URL", example = "https://example.com/profile.jpg")
                    String senderProfileImageUrl,
            @Schema(description = "잔소리 확인여부", example = "false") Boolean isChecked,
            @Schema(description = "잔소리 템플릿") RetrieveTaskFeedbackTemplateDto taskFeedbackTemplate) {
        public static RetrieveTaskFeedbackDto from(TaskFeedbackDto feedback) {
            return new RetrieveTaskFeedbackDto(
                    feedback.id(),
                    feedback.dowithTaskId(),
                    feedback.senderId(),
                    feedback.senderNickname(),
                    feedback.senderProfileImageUrl(),
                    feedback.isChecked(),
                    RetrieveTaskFeedbackTemplateDto.from(feedback.taskFeedbackTemplate()));
        }
    }

    public record RetrieveTaskFeedbackTemplateDto(
            @Schema(description = "잔소리 템플릿 ID", example = "1") Long id,
            @Schema(description = "잔소리 템플릿 언어", example = "ko") CountryCode language,
            @Schema(description = "잔소리 템플릿 메시지", example = "잔소리 템플릿 메시지") String message,
            @Schema(description = "잔소리 템플릿 이모지 URL", example = "https://example.com/emoji.png") String emojiUrl) {
        public static RetrieveTaskFeedbackTemplateDto from(TaskFeedbackTemplateDto template) {
            return new RetrieveTaskFeedbackTemplateDto(
                    template.id(), template.language(), template.message(), template.emojiUrl());
        }
    }
}
