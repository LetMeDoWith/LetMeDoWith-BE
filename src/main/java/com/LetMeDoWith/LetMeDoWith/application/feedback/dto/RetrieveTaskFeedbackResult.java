package com.LetMeDoWith.LetMeDoWith.application.feedback.dto;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.DowithTaskFeedbackQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.TaskFeedbackTemplateQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "두윗모드 Task 잔소리 조회 결과")
public record RetrieveTaskFeedbackResult(Long totalCount, List<TaskFeedbackDto> feedbacks) {

    public static RetrieveTaskFeedbackResult of(
            Long totalCount, List<DowithTaskFeedbackQueryDto> feedbacks, List<TaskFeedbackTemplateQueryDto> templates) {
        List<TaskFeedbackDto> feedbackDtos = feedbacks.stream()
                .map(feedback -> new TaskFeedbackDto(
                        feedback.id(),
                        feedback.dowithTaskId(),
                        feedback.dowithTaskTitle(),
                        feedback.senderId(),
                        feedback.senderNickname(),
                        feedback.senderProfileImageUrl(),
                        feedback.isChecked(),
                        TaskFeedbackTemplateDto.from(templates.stream()
                                .filter(template -> template.id().equals(feedback.taskFeedbackTemplateId()))
                                .findFirst()
                                .get())))
                .toList();

        return new RetrieveTaskFeedbackResult(totalCount, feedbackDtos);
    }

    public record TaskFeedbackDto(
            @Schema(description = "잔소리 ID", example = "1") Long id,
            @Schema(description = "두윗모드 Task ID", example = "12345") Long dowithTaskId,
            @Schema(description = "두윗모드 Task 제목", example = "저녁 러닝하기") String dowithTaskTitle,
            @Schema(description = "잔소리 보낸사람 ID", example = "(TSID)") String senderId,
            @Schema(description = "잔소리 받는사람 닉네임", example = "feedbackSender123") String senderNickname,
            @Schema(description = "잔소리 받는사람 프로필 이미지 URL", example = "https://example.com/profile.jpg")
                    String senderProfileImageUrl,
            @Schema(description = "잔소리 확인여부", example = "false") Boolean isChecked,
            @Schema(description = "잔소리 템플릿") TaskFeedbackTemplateDto taskFeedbackTemplate) {}

    public record TaskFeedbackTemplateDto(
            @Schema(description = "잔소리 템플릿 ID", example = "1") Long id,
            @Schema(description = "잔소리 템플릿 언어", example = "ko") CountryCode language,
            @Schema(description = "잔소리 템플릿 메시지", example = "잔소리 템플릿 메시지") String message,
            @Schema(description = "잔소리 템플릿 이모지 URL", example = "https://example.com/emoji.png") String emojiUrl) {

        public static TaskFeedbackTemplateDto from(TaskFeedbackTemplateQueryDto template) {
            return new TaskFeedbackTemplateDto(
                    template.id(), template.language(), template.message(), template.emojiUrl());
        }
    }
}
