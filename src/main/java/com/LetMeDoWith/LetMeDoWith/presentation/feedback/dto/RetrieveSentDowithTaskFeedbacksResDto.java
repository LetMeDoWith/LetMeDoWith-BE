package com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto;

import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveSentTaskFeedbackResult;
import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveSentTaskFeedbackResult.SentTaskFeedbackDto;
import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.TaskFeedbackTemplateDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "보낸 잔소리 목록 조회 응답")
public record RetrieveSentDowithTaskFeedbacksResDto(
        @Schema(description = "보낸 잔소리 목록") List<SentFeedbackDto> feedbacks) {

    public static RetrieveSentDowithTaskFeedbacksResDto from(RetrieveSentTaskFeedbackResult result) {
        return new RetrieveSentDowithTaskFeedbacksResDto(
                result.feedbacks().stream().map(SentFeedbackDto::from).toList());
    }

    public record SentFeedbackDto(
            @Schema(description = "잔소리 ID", example = "1") Long id,
            @Schema(description = "두윗모드 Task ID", example = "12345") Long dowithTaskId,
            @Schema(description = "두윗모드 Task 제목", example = "저녁 러닝하기") String dowithTaskTitle,
            @Schema(description = "잔소리 받는사람 ID", example = "(TSID)") String receiverId,
            @Schema(description = "잔소리 받는사람 닉네임", example = "feedbackReceiver123") String receiverNickname,
            @Schema(description = "잔소리 받는사람 프로필 이미지 URL", example = "https://example.com/profile.jpg")
                    String receiverProfileImageUrl,
            @Schema(description = "잔소리 확인여부", example = "false") Boolean isChecked,
            @Schema(description = "잔소리 대상 두윗모드 Task 상태", example = "WAIT") DowithTaskStatus dowithTaskStatus,
            @Schema(description = "받는사람 닉네임이 치환된 잔소리 메시지", example = "홍길동 오늘도 달렸나요?") String parsedMessage,
            @Schema(description = "잔소리 템플릿") SentFeedbackTemplateDto taskFeedbackTemplate) {

        public static SentFeedbackDto from(SentTaskFeedbackDto feedback) {
            return new SentFeedbackDto(
                    feedback.id(),
                    feedback.dowithTaskId(),
                    feedback.dowithTaskTitle(),
                    feedback.receiverId(),
                    feedback.receiverNickname(),
                    feedback.receiverProfileImageUrl(),
                    feedback.isChecked(),
                    feedback.dowithTaskStatus(),
                    feedback.parsedMessage(),
                    SentFeedbackTemplateDto.from(feedback.taskFeedbackTemplate()));
        }
    }

    public record SentFeedbackTemplateDto(
            @Schema(description = "잔소리 템플릿 ID", example = "1") Long id,
            @Schema(description = "잔소리 템플릿 언어", example = "ko") CountryCode language,
            @Schema(description = "잔소리명", example = "잔소리명") String name,
            @Schema(description = "잔소리 템플릿 이모지 URL", example = "https://example.com/emoji.png") String emojiUrl) {

        public static SentFeedbackTemplateDto from(TaskFeedbackTemplateDto template) {
            return new SentFeedbackTemplateDto(
                    template.id(), template.language(), template.name(), template.emojiUrl());
        }
    }
}
