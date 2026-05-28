package com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "보낸 잔소리 조회 DTO")
public record SentDowithTaskFeedbackQueryDto(
        @Schema(description = "잔소리 ID", example = "1") Long id,
        @Schema(description = "두윗모드 Task ID", example = "12345") Long dowithTaskId,
        @Schema(description = "두윗모드 Task 제목", example = "저녁 러닝하기") String dowithTaskTitle,
        @Schema(description = "잔소리 템플릿 ID", example = "67890") Long taskFeedbackTemplateId,
        @Schema(description = "잔소리 받는사람 ID", example = "(TSID)") String receiverId,
        @Schema(description = "잔소리 받는사람 닉네임", example = "feedbackReceiver123") String receiverNickname,
        @Schema(description = "잔소리 받는사람 프로필 이미지 URL", example = "https://example.com/profile.jpg")
                String receiverProfileImageUrl,
        @Schema(description = "잔소리 확인여부", example = "false") Boolean isChecked,
        @Schema(description = "잔소리 대상 두윗모드 Task 상태", example = "WAIT") DowithTaskStatus dowithTaskStatus) {}
